"""
    Name: Will Trinh
    ID: 17986840
    Assignment 1 - Text Processing Functions
    Part A: Word Frequencies
    April 18, 2018
"""

import re, sys, time, os  # import system functions, regular expression, time, and misc. operating system modules
from collections import Counter  # import counter to count word frequencies

# Calculating Time Complexity (start time)
start_time = time.time()

# Check if file (argument) exists
if len(sys.argv) is not 2:
    print("Error: Invalid number of arguments.")
    sys.exit(1)  # Exit program with exit code 1
else:
    # Opening and Reading a File
    input_file = sys.argv[1]

    with open(input_file, 'r') as txt:
        # Check whether text file is empty, output error msg and exit program
        if os.stat(input_file).st_size == 0:
            print("Error: Text file is empty.")
            sys.exit(1)
        else:
            str = txt.read().lower()  # Token is independent of capitalization so change everything to lowercase

    # Replace special characters with whitespace and tokenize into a list
    #str = str.replace(u'\ufeff', '') #
    wordList = re.split(r"\W+|_+", str)
    wordList = filter(None, wordList)  # Filter out empty string in list

    # Count word frequency and sorted by highest frequency
    countFreq = Counter(wordList)
    sortedList = sorted(countFreq, key=lambda word: (-countFreq[word], word))
    print(sortedList)
    # Output word frequency in decreasing order and resolve ties alphabetically
    for w in sortedList:                    # Avg Complexity: O(len(sortedList))
        print(w, "-", countFreq[w])

# Calculating Time Complexity (end time)
elapsed_time = time.time() - start_time
print("The program took: ", elapsed_time, "seconds to execute.")
