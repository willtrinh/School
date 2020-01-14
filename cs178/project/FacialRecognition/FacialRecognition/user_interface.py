import os

from pip._vendor.distlib.compat import raw_input

import collect_faces
import cv2
import faces_train
import faces
import operator

if __name__ == '__main__':
    BASE_DIR = os.path.dirname(os.path.realpath(__file__))

    #Welcome prompt
    print('\n\n\n\nWelcome to this Facial Recognition program!')
    #print() # add better description here
    print("We shall begin by collecting pics for your database! Follow the prompts below!\n\n-=================-")

    #build the data base of people's pics for future identification
    #takes 50 pics of each person
    while True:
        #stores a bool for the check was run in order to see if database was updated
        ran_check = collect_faces.collect_faces()# cannot import the three files used because running it in command prompt auto runs it 
        
        #KEY NOTE - this collection method only collects when a face can be detected in the frame
        another = raw_input("Add another face to the database? (yes to proceed): ")
        if another.lower() != 'yes':
            break
        
    #by now, data base is good to go, according to user
    #compile the data in the images data base, only if new data was collected above
    if ran_check:
        print("\n-=================-\n\nTraining Faces...")
        faces_train.train()
        print("Done Training!\n\n-=================-")
    else:
        print('\n-=================-\n\nNo new faces added to database. No need to re-train with same data.\n\n-=================-')

    #run the video capture that detects a face and runs for 50 frames.
    print('\nOpening Face Detector...')
    final_dict = faces.detect_faces()

    print(final_dict)
    print('You are ' + max(final_dict.items(), key = operator.itemgetter(1))[0] + ', with a confidence of ' +str(float(max(final_dict.values()))/float(sum(final_dict.values()))))
