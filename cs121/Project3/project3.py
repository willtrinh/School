import json
from pprint import pprint
from bs4 import BeautifulSoup
import re
import urllib2
import lxml
import numpy
import nltk


identifier = r"C:\Users\willi\Downloads\Dev\cs121\Project 3\WEBPAGES\WEBPAGES_RAW\bookkeeping.json"
with open(identifier, "r") as f:
    data = json.load(f)
i = 1
folder_path = {}
file_path = {}

for path, url in data.items():
    path = path.split("/")
    folder_path[i] = path[0]
    file_path[i] = path[1]
    i = i + 1

length = len(folder_path)
count = 0
while count < length:
    path = r"C:\Users\willi\Downloads\Dev\cs121\Project 3\WEBPAGES\WEBPAGES_RAW\\" + folder_path[count]
    while ()
    with open(path) as f:
        soup = BeautifulSoup(f, "lxml")
    for link in soup.find_all('body', 'head'):
        print(soup.get_text())
    count = count + 1
    f.close()


