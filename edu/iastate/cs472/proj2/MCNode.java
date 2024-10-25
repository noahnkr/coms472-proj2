package edu.iastate.cs472.proj2;

/**
 * Node type for the Monte Carlo search tree.
 * 
 * @author Noah Roberts
 */
public class MCNode<E>
{
  E state; // Current state of the board.
  MCNode<E> parent; // Parent of this node.
  CheckersMove action; // Action that generated this node.
  double score; // Score along the path.

  public MCNode(E state, 
                MCNode<E> parent,
                CheckersMove action, 
                double score) {
    this.state = state;
    this.parent = parent;
    this.action = action;
    this.score = score;
  }
}

