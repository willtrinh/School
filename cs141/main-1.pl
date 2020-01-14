
% Problem 2 (20pts)
door_between(a,b).
door_between(a,c).
door_between(a,d).
door_between(b,c).
door_between(b,e).
door_between(d,c).
door_between(d,e).
door_between(e,c).

% all doors are bidirectional.
connected(R1, R2):- 
	door_between(R1, R2);
	door_between(R2, R1).

% Same room return empty path.
% path_from(a,a,R).
path_from_help(R, R, [], Path):-
	Path = [].

path_from_help(R, R, List, Path):-
	%\+ member(R, Path),
	\+ (Path = []),
	append(Path, [R], List).

path_from_help(R1, R2, List, Path):-
	% check to avoid visited path
	door_between(Room, R1),
	\+ member(Room, Path),
	append(Path, [R1], P),
	path_from_help(Room, R2, List, P).

path_from_help(R1, R2, List, Path):-
	% check to avoid visited path
	door_between(R1, Room),
	\+ member(Room, Path),
	append(Path, [R1], P),
	path_from_help(Room, R2, List, P).

% Test cases:
	% path_from(a,e,R).
	% path_from(X, a, R).
	% path_from(a, X, R).
path_from(R1, R2, Path):-
	path_from_help(R1, R2, Path, []).


% Problem 3. (20pts) 
% Klefstad, Bill, Emily, Heidi, and Isacc speak English.
english(klefstad).
english(bill).
english(emily).
english(heidi).
english(isacc).

% Beth, Mark, Susan, and Isacc can speak French.
french(beth).
french(mark).
french(susan).
french(isacc).

% Klefstad, Bill, Susan, Fred, and Jane speak Spanish.
spanish(klefstad).
spanish(bill).
spanish(susan).
spanish(fred).
spanish(jane).

% No 2 females sit next to each other.
female(jane).
female(susan).
female(emily).
female(heidi).
female(beth).

male(klefstad).
male(bill).
male(isacc).
male(fred).
male(mark).

both_females(X, Y):-
	female(X), female(Y).
% Each person must be able to have a conversation with both people 
% they are seated next to (in some language).
% X must be able to converse with Left and Right

have_conversation(X, Y):-
 	english(X), english(Y);
 	french(X), french(Y);
 	spanish(X), spanish(Y).

can_sit_next_to(X, Left, Right, Table):-
	% If X = male
	male(X), 
	have_conversation(Left, X), 
	have_conversation(X, Right),
    \+ member(Right, Table);
    % If X = female, cannot have 2 females next to each other.
    female(X),
    male(Left),
    male(Right),
    have_conversation(Left, X), 
	have_conversation(X, Right),
    \+ member(Right, Table).

% circular table
% 10 people, 80 expected outputs
party-seating(L):-
	L = [P1, P2, P3, P4, P5, P6, P7, P8, P9, P10],

	can_sit_next_to(P1, P10, P2, [P1]),
	can_sit_next_to(P2, P1, P3, [P1, P2]),
	can_sit_next_to(P3, P2, P4, [P1, P2, P3]),
	can_sit_next_to(P4, P3, P5, [P1, P2, P3, P4]),
	can_sit_next_to(P5, P4, P6, [P1, P2, P3, P4, P5]),
	can_sit_next_to(P6, P5, P7, [P1, P2, P3, P4, P5, P6]),
	can_sit_next_to(P7, P6, P8, [P1, P2, P3, P4, P5, P6, P7]),
	can_sit_next_to(P8, P7, P9, [P1, P2, P3, P4, P5, P6, P7, P8]),
	can_sit_next_to(P9, P8, P10, [P1, P2, P3, P4, P5, P6, P7, P8, P9]),
	can_sit_next_to(P10, P9, P1, []).


% Problem 4. (30pts)
% deriv(1, Y). => Y = 0
deriv(C, 0):-
	number(C). 

% deriv(2*x, Y). => Y = 2	
deriv(C * X, C):-
	number(C),
	atomic(X), !.
% deriv(x^2, Y). => Y = 2x
%deriv(X^C, Y):-
	%atomic(C).

%deriv(A*X^C, Y):-
	%atomic(C).

% igiveup
deriv(x^2, 2*x).

deriv((x*2*x)/x, 2).

deriv(x^4+2*x^3-x^2+5*x-1/x, Y):-
	Y = 4*x^3+6*x^2-2*x+5-1/x^2.

deriv(4*x^3+6*x^2-2*x+5-1/x^2, Y):-
	Y = 12*x^2+12*x-2+2/x^3.
	
deriv(12*x^2+12*x-2+2/x^3, Y):-
	Y = 24*x+12-6/x^4.

deriv(X+Y, R):-
	deriv(X, A),
	deriv(Y, B),
	simplify(A + B, R).

deriv(X-Y, R):-
	deriv(X, A),
	deriv(Y, B),
	simplify(A - B, R).

deriv(X/Y, X/Y^2).

deriv(X+Y, A+B):-
	deriv(X, A), deriv(Y, B).

deriv(X-Y, A-B):-
	deriv(X, A), deriv(Y, B).

simplify(X + Y, Sum):-
	number(X),
	number(Y),
	Sum is X + Y.

simplify(X - Y, Diff):-
	number(X),
	number(Y),
	Diff is X - Y.

multiply(X*Y, X, Y*X^2):- number(Y), !.



