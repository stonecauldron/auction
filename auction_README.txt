



##########################################################################################
##########################################################################################
##################################################            DECENTRALIZED COORDINATION
##########################################################################################
##########################################################################################




//________________________________________________________________      CENTRALIZED PLANNING

/**
it provide a centralized plan for vehicles and tasks and a cost for a plan.
it’s implemented as a singleton. 
TODO : 
for our current centralized algorithm when we converge into local minimum we can shuffle to
a distant situation and begin our optimization again, taking care to remember the best planning encountered.
**/




//____________________________________________ COST ANALYSIS 
/*
for a given task and a given situation, 
the gain analysis class is able to provide the lowest bid we can proposed
in order to have 0 gain / 0 loss : threshold.  
i.e : it computes the marginal cost of taking a task given as parameters
taking care about future
*/




//_______________________________________________________________          ACTION HOUSE HISTORY

/**
it contains a simple list of tasks supplied by the action house until now.
It’s implemented as a singleton.
**/



//_______________________________________________________________                 OPPONENT HISTORY 

/**
it contains the history of proposed bid (so related to the ACTION HOUSE HISTORY),
and a Set<Task> corresponding to the accepted tasks.
**/



//_______________________________________________      OPPONENT STRUCTURE(OPPONENT_HISTORY)

/**
create k model from 3 to 5 vehicle without taking care
about first place vehicle. The Module is able to return
the structure that fit the best on the opponent history.

For the model selected in the class, 
the class maintains a list of difference between the proposed bid of the opponent and the cost
we have performed in the opponent situation assuming the selected model as true.
i.e : if the difference equals 5, so, either the opponent has realized 5 gain on the task
or the opponent has less accuracy of 5. This list will help to predict the bid of the opponent.  

(i.e : we just run centralized planning on opponent history and look if bid are justified)
**/



//_____________________________________________  OPPONENT BID ANALYSIS( OPPONENT_STRUCTURE)

/**
Decorates the OPPONENT_STRUCTURE with the aim to provide the best bid for a task.
The centralized planning singleton informs about the the marginal cost of the task for the opponent. 
Using the relative difference from history to our model we can establish  a distribution function that 
bind a bid to a probability. 

So this module constructs 2 data : 
% => bid
bid => opponentGain (using our model)

With this 2 predictions, plus a threshold 0-gain bid given as parameter (it’s our marginal cost), 
the class is able to supply a bid that maximize the the average difference between our gain and the opponent gain.
i.e : if we loose 3 by taking a task and our opponent win 5 by taking the same task, we do the job (blocking bid).
**/





//____________________________________________ BIDDER( OPPONENT_BID_ANALYSIS, COST_ANALYSIS)

/**
process the bid offer using COST_ANALYSIS and OPPONENT_BID_ANALYSIS :
1. take the threshold from the COST ANALYSIS
2. pass into OPPONENT BID ANALYSIS and get back the optimal bid
it also provides function to update the state of the bidder each times the action house 
is supplying feedback (Bidder.appendResult(ActionHouseResult) => new Bidder). 
**/













