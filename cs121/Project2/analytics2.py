
hostName = {}
outlink = []
f = open("valid_invalid_links.txt", 'r')
ol = open("outlinks.txt", 'a+')

for line in f:
    line = line.replace(' ', '')
    line = line.replace("VALID:from",'')
    line = line.replace("INVALID:from",'')
    tempList = line.split (">>>")
    domain = tempList[0]
    domain = domain.replace('www.', '')
    domain = domain.replace('http://', '')
    domain = domain.replace('https://', '')

    if domain not in hostName:
        hostName[domain] = 1
    else:
        hostName[domain] += 1


sort = sorted(hostName.items(), key = lambda s : s[0])
sortedDomains = sorted(sort, key = lambda sdm: sdm[1], reverse = True)

print ("Page with most outlinks and # of outlinks: ")
maxLink = ""
max = 0
for i in sortedDomains:
    if i[1] > max:
        max = i[1]
        maxLink = i[0]
ol.write(maxLink + ": " + str(max) + "\n")
print(maxLink + ": " + str(max))

f.close()
ol.close()


