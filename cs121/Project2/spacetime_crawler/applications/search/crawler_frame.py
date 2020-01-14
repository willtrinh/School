import logging
from datamodel.search.Wtrinh2Antl2Huyqn6_datamodel import Wtrinh2Antl2Huyqn6Link, OneWtrinh2Antl2Huyqn6UnProcessedLink
from spacetime.client.IApplication import IApplication
from spacetime.client.declarations import Producer, GetterSetter, Getter
import lxml.html
import re, os
from time import time
from uuid import uuid4
from collections import OrderedDict
from urlparse import urlparse, parse_qs
from uuid import uuid4
import requests
from urlparse import urljoin
import os
import sys
# from io import open

logger = logging.getLogger(__name__)
LOG_HEADER = "[CRAWLER]"

@Producer(Wtrinh2Antl2Huyqn6Link)
@GetterSetter(OneWtrinh2Antl2Huyqn6UnProcessedLink)
class CrawlerFrame(IApplication):
    app_id = "Wtrinh2Antl2Huyqn6"

    def __init__(self, frame):
        self.app_id = "Wtrinh2Antl2Huyqn6"
        self.frame = frame


    def initialize(self):
        self.count = 0
        links = self.frame.get_new(OneWtrinh2Antl2Huyqn6UnProcessedLink)
        if len(links) > 0:
            print "Resuming from the previous state."
            self.download_links(links)
        else:
            l = Wtrinh2Antl2Huyqn6Link("http://www.ics.uci.edu/")
            print l.full_url
            self.frame.add(l)

    def update(self):
        # unprocessed_links = None
        # while not unprocessed_links:
        unprocessed_links = self.frame.get_new(OneWtrinh2Antl2Huyqn6UnProcessedLink)
        if unprocessed_links:
            self.download_links(unprocessed_links)
        else:
            os.execv(sys.executable, ['python'] + sys.argv)

    def download_links(self, unprocessed_links):
        for link in unprocessed_links:
            f = open("unprocessed_links.txt","a+")
            f.write("VALID: from " + link.full_url + "\n")
            f.close()
            try:
                # if (is_valid(link.full_url)): # THIS IF IS FOR TESTING PURPOSE ONLY
                print "Got a VALID link to download:", link.full_url
                downloaded = link.download()
                links = extract_next_links(downloaded)
                for l in links:
                    if is_valid(l):
                        f = open("valid_links.txt","a+")
                        f.write(link.full_url + " >>> " + l + "\n")
                        f.close()
                        self.frame.add(Wtrinh2Antl2Huyqn6Link(l))
                    else:
                        # Avoid erro with invalid urls containing utf8
                        try:
                            link.full_url.decode('ascii')
                            f = open("invalid_links.txt","a+")
                            f.write(link.full_url + " >>> " + l + "\n")
                            f.close()
                        except UnicodeDecodeError:
                            return False
                        except UnicodeEncodeError:
                            return False
                       
            except IOError:
                return

    def shutdown(self):
        print (
            "Time time spent this session: ",
            time() - self.starttime, " seconds.")
    
def extract_next_links(rawDataObj):
    if (rawDataObj.content != '' and rawDataObj.http_code == 200):
        if (rawDataObj.is_redirected):
                # normalize the url before comparing by lowering case and remove last backslash
                original = rawDataObj.url.lower()
                original = original[:-1] if original[-1] == '/' else original
                last = rawDataObj.final_url.lower()
                last = last[:-1] if last[-1] == '/' else last
                if (original != last):
                    print "ENCOUNTER A REDIRECT \n"
                    return [rawDataObj.final_url]
        
        # Continue if the page is not redirected or is redirected to the same final url
        root = lxml.html.fromstring(rawDataObj.content)
        # Make all links absolute 
        root.make_links_absolute(rawDataObj.url, resolve_base_href=True)
        # Get all links
        links = root.xpath('//body//a/@href')
        # Put the links into a hash list (OrderedDict) to remove duplicate
        uniquelinks = list(OrderedDict.fromkeys([x[:-1].lower() if x[-1] == '/' else x.lower()  for x in links]))
        
        return uniquelinks
    else:
        return []

def is_valid(url):
    parsed = urlparse(url)

    # invalid all urls containing utf8
    try:
        url.decode('ascii')
    except UnicodeDecodeError:
        return False
    except UnicodeEncodeError:
        return False

    ## ANALYSIS
    ## STORE HOST NAME AND ADDRESS for LATER ANALYTIC
    if parsed.hostname:
        if  ".ics.uci.edu" in parsed.hostname:
            f = open("hosts.txt","a+")
            f.write(parsed.hostname + " >>> " + url + "\n")
            f.close()


    if parsed.scheme not in set(["http", "https"]):
        return False

    url = url.lower()

    # avoid link with 3 or more same paths directory /same/a/b/same/c/same/ or even /same/same/same
    if re.match('(\/\w+?\/?).+?\1.+?\1', url):
        return False

    # avoid links with 4 or more than parameters
    if re.match('(.*\=.*){4,}', url):
        return False

    # # avoid calendar link
    # if re.match('.*calendar.*', url):
    #     return False

    # # avoid the presence of year (4 digits) together with a month or date or both (2 digits)
    # if re.match('(\D+?(\d{4}|\d{1,2})\D??){2,}', url):
    #     return False

    # avoid the presence of year (4 digits) together with a month or date or both (2 digits)
    if re.match('.*(\d{4}-\d{1,2}-\d{1,2}).*', url):
        return False

    # # avoid the common parameters year month day
    # if re.match('.*(year=|day=|month=).*', url):
    #     return False

    # avoid id-link #
    if re.match('.*#.*', url):
        return False

    # additional extension to avoid
    extensions = "|r|c"

    try:
        return ".ics.uci.edu" in parsed.hostname \
            and not re.match(".*\.(css|js|bmp|gif|jpe?g|ico" + "|png|tiff?|mid|mp2|mp3|mp4"\
            + "|wav|avi|mov|mpeg|ram|m4v|mkv|ogg|ogv|pdf" \
            + "|ps|eps|tex|ppt|pptx|doc|docx|xls|xlsx|names|data|dat|exe|bz2|tar|msi|bin|7z|psd|dmg|iso|epub|dll|cnf|tgz|sha1" \
            + "|thmx|mso|arff|rtf|jar|csv"\
            + extensions\
            + "|rm|smil|wmv|swf|wma|zip|rar|gz|pdf)$", parsed.path.lower())

    except TypeError:
        print ("TypeError for ", parsed)
        return False