; Name: Will Trinh
; ID: 17986840
; CS 141 - Programming Languages
; Professor Klefstad
; Homework 4


; Allowed functions: 
;defun, cond, cons, car, 
;cdr, null, eq, listp, atom, symbolp, +, -, <, and >

; Exercise 1
(defun my-length (L)
    (cond
        ; if list is empty return 0
        ((null L) 0)
        ; else recursively count # of elem in list
        (t (+ 1 (my-length (cdr L))))))


; (print(my-length nil)); --> 0
; (print(my-length '(b (a b c)))); --> 2
; (print(my-length '(a (((b))) c))); --> 3
; (print(my-length '(a b c))); --> 3
; (terpri)


; Exercise 2
(defun my-memq (A L)
    (cond
        ; check if L is empty
        ((null L) nil)
        ; check if A equal to first elem in L
        ((eq A (car L)) L)
        ; else recursively check the rest of L
        (t (my-memq A (cdr L)))))
        
; (print(my-memq 'a nil)); --> nil
; (print(my-memq 'b '(a b c))); --> (b c)
; (print(my-memq 'd '(a b c d e f g))); --> (d e f g)
; (print(my-memq 'd '(a b c d))); --> (d)
; (print(my-memq 'd '(a b c))); --> nil
; (terpri)


; Exercise 3
(defun my-append (L1 L2)
    (cond
        ; check if L1/L2 is empty
        ((null L1) L2)
        ((null L2) L1)
        ; else resursively cons L1 starting from the last element into L2
        (t (cons (car L1) (my-append (cdr L1) L2)))))

; (print(my-append '(a b c) '(d e f))); --> (a b c d e f)
; (print(my-append '((a) (b) (c)) '((d) (e) (f)))); --> ((a) (b) (c) (d) (e) (f))
; (print(my-append nil '(d e f))); --> (d e f)
; (print(my-append '(a b c) nil)); --> (a b c)
; (terpri)


; Exercise 4
(defun my-attach (O L)
    (cond
        ; check if L is null, cons O into L 
        ((null L) (cons O L))
        ; else recursively cons O into cdr of L
        (t (cons (car L) (my-attach O (cdr L))))))

; (print(my-attach 'a nil)) ; --> (a)
; (print(my-attach 'd '(a b c))) ; --> (a b c d)
; (print(my-attach '(a) '(b c))) ; --> (b c (a))
; (terpri)


; Exercise 5
(defun my-assoc (A L)
    (cond
        ((null L) nil)
        ((eq A (car(car L))) (car L)) 
        (t (my-assoc A (cdr L)))))

; (print(my-assoc 'a nil)); --> nil
; (print(my-assoc 'a '((a . b)(c e f)(b)))); --> (a . b)
; (print(my-assoc 'c '((a . b)(c e f)(b)))); --> (c e f)
; (print(my-assoc 'b '((a . b)(c e f)(b)))); --> (b)
; (print(my-assoc 'f '((a . b)(c e f)(b)))); --> nil
; (terpri)


; Exercise 6
(defun atom-helper (L)
    (cond 
        ; if L is atom, return L
        ((atom L) L)
        ; else recurse through L
        (t (atom-helper (car L)))))
(defun cdr-helper (L)
    (cond
        ; check if car L is an atom, return cdr if true
        ((atom (car L)) (cdr L))
        ; check if L is a list of 1 element, return cdr if true
        ((null (cdr-helper (car L))) (cdr L))
        ; else resursively cons first car with its cdr
        (t (cons (cdr-helper (car L)) (cdr L)))))

(defun freq (A L)
    (cond 
        ;check if L is empty
        ((null L) 0)
        ; increment frequency if first atom of L is equal A
        ((eq A (atom-helper L)) (+ 1 (freq A (cdr-helper L))))
        ; else recurse and check the rest of the list
        (t (freq A (cdr-helper L)))))

; (print(freq 'c '((a c) c e))); --> 2
; (print(freq 'f '(((s) o ) d))); --> 0
; (print(freq 'f '(((f) f) f f))); --> 4
; (terpri)


; Exercise 7
(defun mapping (L val)
    (cond
        ; check if L is empty
        ((null L) nil)
        ((atom (car L))
            ; if key < val, return list of objects 
            (cond ((< (car L) val) (car (cdr L)))))
        ; recursively go through L if key > val
        ((null (mapping (car L) val)) (mapping (cdr L) val))
        ; else recursively make a list of objects through L lists
        (t (cons (mapping (car L) val) (mapping (cdr L) val)))))

; (print(mapping '((35 kim) (67 clinton) (45 emma)) 40)); --> (kim)
; (print(mapping '((24 a) (15 b) (56 c) (19 d)) 26)); --> (a b d)
; (print(mapping '((90 a) (80 b) (70 c)) 40)); --> nil
; (terpri)


; Exercise 8
(defun my-last (A L)
    (cond
        ; check if L is empty
        ((null L) nil)
        ; call my-memq on cdr L, if it is null then it is the last occurrence.
        ((null (my-memq A (cdr L))) (my-memq A L))
        ; else recurse through the list
        (t (my-last A (cdr L)))))

; (print(my-last 'a '(a b c a b c a b c d e f g))); --> (a b c d e f g)
; (print(my-last 'b '(a b c a b c a b c d e f g))); --> (b c d e f g)
; (print(my-last 'c '(a b c a b c a b c d e f g))); --> (c d e f g)
; (print(my-last 'g '(a b c a b c a b c d e f g))); --> (g)
; (print(my-last 'h '(a b c a b c a b c d e f g))); --> nil
; (terpri)


; Exercise 9
(defun my-reverse (L) 
    (cond 
        ; checks if L is empty
        ((null L) nil)
        ; checks if L is a list, if true reverse the list
        ((listp (car L)) (my-attach (car L) (my-reverse (cdr L))))
        ; else call my-attach to create new list
        (t (my-attach (car L) (my-reverse (cdr L))))))

; (print(my-reverse nil)); --> nil
; (print(my-reverse '(a))); --> (a)
; (print(my-reverse '(1 2 3 4 5))); --> (5 4 3 2 1)
; (print(my-reverse '((1 2 3) 4 ((5 6))))); --> (((5 6)) 4 (1 2 3))
; (terpri)


; Exercise 10
(defun pattern-help(pat str)
    (cond 
        ; check if pattern is an atom, if true check if equal to first character in string
        ((atom pat) (eq pat (car str)))
        ; check if cdr of pattern is null, if true check if equal
        ((null (cdr pat)) (eq (car pat) (car str)))
        ; else recursively check for each letter in pattern against the string
        (t (and (pattern-help (car pat) str) (pattern-help (cdr pat) (cdr str))))))

(defun is-pattern?(pat str)
    (cond 
        ; check if string is null
        ((null (car str)) nil)
        ; check if pattern is substring
        ((pattern-help pat str) str)
        ; else recurse through string
        (t (is-pattern? pat (cdr str)))))

; (print(is-pattern? '(a b s) '(c d b a s))); --> nil
; (print(is-pattern? '(c a c) '(b a j a c a c t u s))); --> (c a c t u s)
; (print(is-pattern? nil '(a n y l i s t))); --> nil
; (print(is-pattern? '(l i s p) nil)); --> nil
; (terpri)


; Exercise 11
(defun first-atom (L)
    (cond
        ; check if L is an atom, return L if true
        ((atom L) L)
        ; else L is a list, recurse through L
        (t (first-atom (car L)))))

; (print(first-atom nil)); --> nil
; (print(first-atom '((2 (1) 4) 6))); --> 2
; (print(first-atom '((((s)) o )))); --> s
; (print(first-atom '(1 (((2)) 3 4)))); --> 1
; (terpri)


; Exercise 12
(defun find-all (A L)
    (cond 
        ; checks if list is empty
        ((null L) nil)
        ; check if A is equal first atom, 
        ; if true recursively cons the first atom of cdr L to the rest of the list
        ((eq A (first-atom L)) (cons (first-atom (cdr-helper L)) (find-all A (cdr-helper L))))
        ; else recurse through L
        (t (find-all A (cdr-helper L)))))
        
; (print(find-all 'a nil)); --> nil
; (print(find-all 'a '(b a c a e))); --> (c e)
; (print(find-all 'a '(b d c e))); --> nil
; (print(find-all 'a '(b (a a) c))); --> (a c)
; (print(find-all 'a '((b a) ((c a b))))); --> (c b)
; (terpri)


