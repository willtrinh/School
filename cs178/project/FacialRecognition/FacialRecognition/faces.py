import numpy as np
import cv2
import pickle
import numpy as np
from collections import defaultdict
import os

def detect_faces():
    BASE_DIR = os.path.dirname(os.path.abspath(__file__))

    face_cascade = cv2.CascadeClassifier(BASE_DIR + '/cascades/data/haarcascade_frontalface_alt2.xml')
    eye_cascade = cv2.CascadeClassifier(BASE_DIR + '/cascades/data/haarcascade_eye.xml')

    recognizer = cv2.face.LBPHFaceRecognizer_create()
    recognizer.read('trainer.yml')

    names = {}
    with open("names.pickle", 'rb') as f: # read bytes
        names_1 = pickle.load(f) #load name_ids from "labels" pickle file
        names = {v:k for k, v in names_1.items()} #invert
    cap = cv2.VideoCapture(0)

    final_dict = defaultdict(int)

    while(True):
        #capture frame by frame
        ret, frame = cap.read()
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY) #convert everything to gray to work with frontalface_alt2. looks for the front of faces
        faces = face_cascade.detectMultiScale(gray, scaleFactor = 1.5, minNeighbors = 5)

        #iterate through faces
        for (x, y, w, h) in faces:
            #x and y are original coords of face (most likely top left of rectangle). w and h (width and height) are what are added to the original x and y to complete the rectangle around the face
            #print(x,y,w,h) #stops printing when covering camera because doesnt recognize a face
            
            #y is originial y coord, y+h is coord across the rectangle. same for x and w. height and width
            roi_gray = gray[y:y+h, x:x+w]         #region of interest for gray scale frame
            roi_color = frame[y:y+h, x:x+w]          #region of interest for color frame
            
            # RECOGNIZE -  deep learned model
            id_, conf = recognizer.predict(roi_gray) #gives the name back and the confidence
            if conf >= 4 and conf <=85:
                #print(id_)
                print(names[id_])
                cv2.putText(frame, names[id_], (x, y), cv2.FONT_HERSHEY_SIMPLEX , 1, (255,255,255), 2, cv2.LINE_AA) # write name next to person in frame when recognized
                
                final_dict[names[id_]] += 1 #for this frame, the person was identified as someone. add this identification to the final dict 
            
            #img_item = BASE_DIR + '/my-image.png'          #save it to this image in this directory
            #cv2.imwrite(img_item, roi_gray)                 #saves a pic of just my face in gray (no background, just the face rectangle)
            #cv2.imwrite(img_item, roi_color)                #saves a pic of just my face in color (no background, just the face rectangle)

            color = (255, 102, 255)                  #not rgb, is bgr. defines color of rectangle to be drawn around the face
            stroke = 2
            end_coord_x = x + w      #end coord of original x plus width
            end_coord_y = y + h      #end coord of original y plus height

            cv2.rectangle(frame, (x,y), (end_coord_x, end_coord_y), color, stroke)

            eyes = eye_cascade.detectMultiScale(roi_gray) #detect eyes in the frame in gray
            for (ex, ey, ew, eh) in eyes:
                cv2.rectangle(roi_color, (ex, ey), (ex+ew, ey+eh), (0, 255, 0), 2)
       
        #Display the resulting frame
        cv2.imshow('frame', frame)
        if cv2.waitKey(20) & 0xFF == ord('q'):             #displays in color mode even though you are detecting in gray (set to gray earlier)
            break

    #when everything is done, release the capture
    cap.release()
    cv2.destroyAllWindows()
    return dict(final_dict)


if __name__ == '__main__':
    print('This should not print')
