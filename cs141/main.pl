% 1a. ?- my_reverse([a,b,c,d], R).
% R = [d,c,b,a]
% Recursively append head of old list to be head of new list => order is reversed.
reverse_help([], L, L).
reverse_help([Head|Tail], L, RevList) :- 
	reverse_help(Tail, [Head|L], RevList).

% ?- my_reverse([], B).
% Base case, if empty list return empty list
my_reverse([], []). 
% Else call helper to create a new list with elems in reversed order.
my_reverse([Head|Tail], RevList) :-
	reverse_help([Head|Tail], [], RevList).


% b. ?- my_length([[a,b],[c,d],[e,f]], Y).
% Y = 3
% ?- my_length([], G). => G = 0
% ?- my_length([a, b, c, d], B). => B = 4
% Base case, empty list.
my_length([], 0).
my_length([_|Tail], Length) :-
	my_length(Tail, Count),
	Length is Count + 1.


% c. ?- my_subset(atom, [a,[b],c], Y).
% Y = [a,c]
% ?- my_subset(atom, [x, y, [z], w], G).
my_subset(_, [], []).
% Use =.. operator to evaluate functor. 
% atom(a), atom(c) are true, add to list.
% atom([b]) is false. 
my_subset(Atom,[Head|Tail],[Head|NewTail]) :-
    Term =..[Atom, Head],
    Term,
    my_subset(Atom,Tail,NewTail).
 
my_subset(Atom,[_|Tail],NewTail) :-
    my_subset(Atom,Tail,NewTail).


% d. ?- my_member(a,[a,b,c]).
% Yes
% ?- my_member(Y,[a,b,c]).
% Y = a;
% Y = b;
% Y = c;
% No
% Base case, if variable eq head then it is a member.
my_member(Head, [Head|_]).
% Recursively compare X with the tail of the list.
my_member(Head, [_|Tail]) :-
	my_member(Head, Tail).


% e. ?- my_intersect([a,b,c],[[c],d,b,e,a], R).
% R = [a,b]
% my_intersect([a,b,c],[[c],d,b,e,a], R)
my_intersect([], _, []).
% Call my_member to check if each element is a member of the list
my_intersect([Head|Tail], L, [Head|NewTail]) :-
	my_member(Head, L),
	my_intersect(Tail, L, NewTail).
my_intersect([_|Tail], L, Intersect) :-
	my_intersect(Tail, L, Intersect).

%2a. ?- compute-change(33,Q,D,N,P).
% compute-change(182,Q,D,N,P).
% Use highest value money first to minimize number of coins used. Greedy algorithm.
% Quarter = 25
% Dime = 10
% Nickle = 5
% Penny = 1
compute-change(0, 0, 0, 0, 0).
compute-change(Money, Quarter, Dime, Nickle, Penny) :-
	% Use Quarter when..
	Money >= 25,
	Remains is Money - 25,
	compute-change(Remains, Q, Dime, Nickle, Penny),
	Quarter is Q + 1.
compute-change(Money, Quarter, Dime, Nickle, Penny) :-
	% Use Dime when..
	Money < 25, Money >= 10,
	Remains is Money - 10,
	compute-change(Remains, Quarter, D, Nickle, Penny),
	Dime is D + 1.
compute-change(Money, Quarter, Dime, Nickle, Penny) :-
	% Use Nickle when..
	Money < 10, Money >= 5,
	Remains is Money - 5,
	compute-change(Remains, Quarter, Dime, N, Penny),
	Nickle is N + 1.
compute-change(Money, Quarter, Dime, Nickle, Penny) :-
	% Use Penny when..
	Money < 5, Money > 0,
	Remains is Money - 1,
	compute-change(Remains, Quarter, Dime, Nickle, P),
	Penny is P + 1.


% b. ?- compose([a,b,c],[x,y,z],L).
% L = [a,x,b,y,c,z]
% ?- compose([a,b,c],[x,y],L).
% L = [a,x,b,y,c]
% ?- compose(L1,L2,[a,b,c]).
% L1 = [] L2 = [a,b,c] ;
% L1 = [a] L2 = [b,c] ;
% L1 = [a,c] L2 = [b];
% L1 = [a,b,c] L2 = []
% compose(L1, L2, L3)
compose([], L, L).
compose(L, [], L).
% compose L1 head1 and L2 head2 into compose list, 
% then recursively traverse each list and compose head1 head2 in that order
% to compose list.
compose([Head1|Tail1], [Head2|Tail2], [Head1, Head2|ComposeList]) :-
	compose(Tail1, Tail2, ComposeList).


% c. ?- palindrome([m,a,d],R).
% R = [m,a,d,d,a,m] ;
% no
% ?- palindrome(B,[n,i,s,s,i,n]).
% B = [n,i,s] ;
% no
% palindrome(Base, Result)
% Result is even length list
% reverse the list and append the reverse of the list to new list.
% Base case, empty list
palindrome([], []).
palindrome(Base, Result) :-
	% Call my_reverse to make a new reverse list
	my_reverse(Base, Rev),
	% Append reverse list into base list.
	append(Base, Rev, Result).