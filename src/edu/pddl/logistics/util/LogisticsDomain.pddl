(define (domain logistics)
	(:requirements :typing :equality :action-costs :durative-actions)
	(:types
		CONTAINER CARGO - object
		TRANSPORT LOCATION - CONTAINER
	)
	(:predicates
		(at ?x - (either TRANSPORT CARGO) ?y - LOCATION)
		(in ?x - CARGO ?y - TRANSPORT)
		(ready-loading ?x - TRANSPORT)
	)
	(:functions
		(remaining-capacity ?x - CONTAINER) - number
		(current-inventory ?x - CONTAINER) - number
		(travel-time ?x - TRANSPORT ?y ?z - LOCATION) - number
		(size ?x - CARGO) - number
		(load-time ?x - TRANSPORT) - number
		(unload-time ?x - TRANSPORT) - number
		(available-in ?x - (either TRANSPORT CARGO)) - number
		(total-cost) - number
	)
	(:durative-action load
		:parameters (?x - TRANSPORT ?y - CARGO ?z - LOCATION)
		:duration (= ?duration (load-time ?x))
		:condition	(and
			(over all (at ?x ?z))
			(at start (ready-loading ?x))
			(at start (at ?y ?z))
			(at start (<= (size ?y) (remaining-capacity ?x)))
			(over all (= (available-in ?x) 0))
			(over all (= (available-in ?y) 0)))
		:effect	(and
			(at start (not (at ?y ?z)))
			(at start (decrease (current-inventory ?z) (size ?y)))
			(at start (decrease (remaining-capacity ?x) (size ?y)))
			(at start (not (ready-loading ?x)))
			(at end (increase (remaining-capacity ?z) (size ?y)))
			(at end (increase (current-inventory ?x) (size ?y)))
			(at end (in ?y ?x))
			(at end (ready-loading ?x))
			(at end (increase (total-cost) (load-time ?x)))))
	(:durative-action unload
		:parameters (?x - TRANSPORT ?y - CARGO ?z - LOCATION)
		:duration (= ?duration (unload-time ?x))
		:condition	(and
			(over all (at ?x ?z))
			(at start (ready-loading ?x))
			(at start (in ?y ?x))
			(at start (<= (size ?y) (remaining-capacity ?z)))
			(over all (= (available-in ?x) 0))
			(over all (= (available-in ?y) 0)))
		:effect	(and
			(at start (not (in ?y ?x)))
			(at start (decrease (remaining-capacity ?z) (size ?y)))
			(at start (decrease (current-inventory ?x) (size ?y)))
			(at start (not (ready-loading ?x)))
			(at end (increase (current-inventory ?z) (size ?y)))
			(at end (increase (remaining-capacity ?x) (size ?y)))
			(at end (at ?y ?z))
			(at end (ready-loading ?x))
			(at end (increase (total-cost) (unload-time ?x)))))
	(:durative-action move
		:parameters (?x - TRANSPORT ?y ?z - LOCATION)
		:duration (= ?duration (travel-time ?x ?y ?z))
		:condition	(and
			(at start (at ?x ?y))
			(at start (>= (travel-time ?x ?y ?z) 0))
			(at start (not (= ?y ?z)))
			(over all (= (available-in ?x) 0)))
		:effect	(and
			(at start (not (at ?x ?y)))
			(at end (at ?x ?z))
			(at end (increase (total-cost) (travel-time ?x ?y ?z)))))
	(:durative-action wait
		:parameters (?x - (either TRANSPORT CARGO) ?y - LOCATION)
		:duration (= ?duration 1)
		:condition (and
			(at start (> (available-in ?x) 0))
			(over all (at ?x ?y)))
		:effect (and
			(at end (decrease (available-in ?x) 1))))
)
