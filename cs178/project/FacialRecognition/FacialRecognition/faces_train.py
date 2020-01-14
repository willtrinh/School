# takes a data base (directory) comprised of folders of people's names with their pictures inside
# learns what each person's frontal face looks like through training
# saves results to trainer.yml, to be used in faces.py in recognition

import os
import cv2
from PIL import Image # takes a image and gets the numbers of the pixels (2d array)
import numpy as np
import pickle

def train():
    BASE_DIR = os.path.dirname(os.path.abspath(__file__))  # path/directory in which this python file is in
    image_dir = os.path.join(BASE_DIR, "images") # path of folder "images" (includes the directory it is in, same dir as this python file)

    face_cascade = cv2.CascadeClassifier(BASE_DIR + '/cascades/data/haarcascade_frontalface_alt2.xml')


    current_id = 0 #for every new name id that is created, add one to value
    name_ids = dict()
    x_train = [] # each list of pics of 
    y_names = []

    for root, dirs, files, in os.walk(image_dir) : #walk through every image in everone's folder in images folder
        for file in files:
            if file.endswith("png") or file.endswith("jpg"):
                path = os.path.join(root, file) # full path to pic
                person_name = os.path.basename(root).replace(" ", "-").lower() # name of folder pic is in (person's name)
                #print(person_name, path)

                #NEED TO :
                #y_names.append(person_name) # add name to y_names
                #x_train.append(path) #add path of pic to x_train

                #BUT FIRST, NEED TO :            
                #for names in y_names, need to convert names into numbers first
                if not person_name in name_ids: 
                    name_ids[person_name] = current_id
                    current_id+=1
                id_ = name_ids[person_name]
                #print(name_ids)
                
                #for pics in x_train, need to first verify the image, make it gray to work with the classifier, and turn it into numpy array 
                pil_image = Image.open(path).convert("L") # image object created with the pic from the path, converted to gray
                final_image = pil_image.resize((550, 550), Image.ANTIALIAS) # resize to improve accuracy in identification
                image_array = np.array(final_image, "uint8") # convert image object to numpy array, 2d array of pixel values
                #print(image_array)
                
                faces = face_cascade.detectMultiScale(image_array, scaleFactor = 1.5, minNeighbors = 5)
                #iterate through faces
                for (x, y, w, h) in faces: # 4 coords to make a rectangle-> x, y, x+w, y+h
                    roi = image_array[y:y+h, x:x+w] #region of interest (face), calculated with a rectangle made with 4 coordinates.
                    x_train.append(roi) # append region of interest to x_train
                    y_names.append(id_) #converted id from above
    #print(y_names)
    #print(len(y_names))
    #print(len(x_train))
    with open('names.pickle', 'wb') as f: #writing bytes
        pickle.dump(name_ids, f)

    #create recognizer
    recognizer = cv2.face.LBPHFaceRecognizer_create()

    recognizer.train(x_train, np.array(y_names)) #train
    recognizer.save('trainer.yml') #save results to trainer.yml

if __name__ == '__main__':
    print('this should not run')

            
