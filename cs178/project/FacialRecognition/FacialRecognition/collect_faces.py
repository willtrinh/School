import numpy as np
import cv2
import pickle
import numpy as np
import os
import sys
from collections import defaultdict

from pip._vendor.distlib.compat import raw_input


def collect_faces():
    BASE_DIR = os.path.dirname(os.path.realpath(__file__))
    face_cascade = cv2.CascadeClassifier(BASE_DIR + '/cascades/data/haarcascade_frontalface_alt2.xml')

    cap = cv2.VideoCapture(0)
    counter = 1
    name = raw_input("Please Enter Your Name (ie - John Smith, q to quit):")
    if name == 'q':
        return False # if didnt run all the way and quit early, return false
    try:#make person's name a folder in images
        os.makedirs(BASE_DIR + '/IMAGES/' + name)
    except: 
        print('\nSIDE NOTE - This person\'s name is already in images. Will overwrite images instead\n')
    ready = raw_input('Ready to proceed? (Enter any key to continue): ')
    print('\nStarting to take pictures... (ends at 50)')
    while(True):
        if counter == 51:
            cap.release()
            cv2.destroyAllWindows()
            return True # if ran all the way, return true
        #capture frame by frame
        ret, frame = cap.read()
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY) #convert everything to gray to work with frontalface_alt2. looks for the front of faces
        faces = face_cascade.detectMultiScale(gray, scaleFactor = 1.5, minNeighbors = 5)
        
        if len(faces) > 0: # only save a pic of someone if their face is detectable
            img_item = BASE_DIR + '/Images/' + name + '/' + str(counter) + '.png'          #save it to this image in this directory
            cv2.imwrite(img_item, frame)                 #saves a pic of just my face in gray (no background, just the face rectangle)
            #cv2.imwrite(img_item, roi_color)                #saves a pic of just my face in color (no background, just the face rectangle)
            if counter % 10 == 0:  #print every 10 writes
                print(str(counter) + ' pictures done!')
            elif counter%3 == 0: #print a . every 3 writes
                print('.')
            counter += 1
        else:
            print('-={FACE NOT DETECTED}=-')
            
        #Display the resulting frame
        cv2.imshow('frame', frame)
        if cv2.waitKey(20) & 0xFF == ord('q'):             #displays in color mode even though you are detecting in gray (set to gray earlier)
            break

    #when everything is done, release the capture
 

if __name__ == '__main__':
    print('this should not run')


