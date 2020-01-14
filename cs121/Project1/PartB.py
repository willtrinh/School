"""
    Name: Will Trinh
    ID: 17986840
    Assignment 1 - Text Processing Functions
    Part B: Intersection of two files
    April 18, 2018
"""

import re, sys, time, os  # import system functions, regular expression, time, and misc. operating system modules

# Calculating Time Complexity (start time)
start_time = time.time()

# Check if file (arguments) exist
if len(sys.argv) is not 3:
    print("Error: Invalid number of arguments.")
    sys.exit(1)  # Exit program with exit code 1
else:
    # Opening and Reading Two Files
    file1 = sys.argv[1]
    file2 = sys.argv[2]

    with open(file1, 'r') as txt1, open(file2, 'r') as txt2:
        # Check whether text file is empty, output error msg and exit program
        if os.stat(file1).st_size == 0 or os.stat(file2).st_size == 0:
            print("Error: Text file is empty.")
            sys.exit(1)
        else:
            str1 = txt1.read().lower()  # Token is independent of capitalization so change everything to lowercase
            str2 = txt2.read().lower()

    # Replace special characters with whitespace and tokenize into 2 separated lists
    wordList1 = re.split(r"\W+|_+", str1)
    wordList1 = filter(None, wordList1)  # Filter out empty string in list
    wordList2 = re.split(r"\W+|_+", str2)
    wordList2 = filter(None, wordList2)

    # Set intersection wordList1 & wordList2
    wordList = set(wordList1) & set(wordList2)  # Avg Complexity: O(min(len(wordList1), len(wordList2))

    count = 0  # Number of tokens in common

    # Print out common words and number of matches
    for w in wordList:                          # Avg Complexity: O(len(wordList))
        print(w)
        count += 1
    print("\nMatches Found:", count)

# Calculating Time Complexity (end time)
elapsed_time = time.time() - start_time
print("The program took:", elapsed_time, "seconds to execute.")

# Overall Avg Complexity: O(len(wordList))
