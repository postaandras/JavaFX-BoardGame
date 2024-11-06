package controller;
import game.TwoPhaseMoveState;
import lombok.Getter;
import model.Move;
import model.Position;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;


public class ColorGameState implements TwoPhaseMoveState<Position> {
    private Player nextPlayer;
    @Getter
    private final Player[][] board;
    @Getter
    private static final int BOARD_SIZE = 5;
    @Getter
    List<Position> player1Positions = new ArrayList<>();
    @Getter
    List<Position> player2Positions = new ArrayList<>();
    @Getter
    List<Position> currentAvailableMoves = new ArrayList<>();
    @Getter
    LinkedList<Move> previousMoves = new LinkedList<>();


    public ColorGameState() {
        board = new Player[BOARD_SIZE][BOARD_SIZE];
        nextPlayer = Player.PLAYER_1;
        initializeBoard();
    }
    private void initializeBoard() {
        for(int i =0; i<BOARD_SIZE; i++){
            for(int j=0; j<BOARD_SIZE; j++){

                if(i==0 && j==4 || i==2 && j==2 || i==4 && j==0){
                    board[i][j] = Player.PLAYER_1;
                    player1Positions.add(new Position(i,j));
                }
                else{
                    board[i][j] = Player.PLAYER_2;
                    player2Positions.add(new Position(i,j));
                }
            }
        }
    }

    @Override
    public Player getNextPlayer(){
        return nextPlayer;
    }

    public void switchPlayer(){
        nextPlayer= nextPlayer.opponent();
    }

    @Override
    public String toString(){
        var sb = new StringBuilder();
        appendColumnHeaders(sb);
        appendRows(sb);
        return sb.toString();
    }

    private void appendColumnHeaders(StringBuilder sb){
        sb.append("  ");
        for(var i = 0; i<BOARD_SIZE; i++){
            sb.append(" ").append(i);
        }

        sb.append("\n");
    }

    private void appendRows(StringBuilder sb){
        for (var i = 0; i <BOARD_SIZE; i++) {
            sb.append(" ").append(i).append(" ");
            for (var j = 0; j <BOARD_SIZE; j++) {
                sb.append(board[i][j] == Player.PLAYER_1? "B" :
                        board[i][j] == Player.PLAYER_2? "R" :
                                '_');
                sb.append(' ');
            }
            sb.append('\n');
        }
    }


    public boolean isPositionOufOfBounds(Position position){
        return(position.col()>BOARD_SIZE-1 || position.row()>BOARD_SIZE-1 ||
                position.col()<0 || position.row()<0);
    }
    @Override
    public boolean isLegalToMoveFrom(Position position) {
        if(isPositionOufOfBounds(position)){
            return  false;
        }

        Player tileOccupant = board[position.col()][position.row()];

        if(tileOccupant != nextPlayer){
            return false;
        }

        return isNotStranded(position, nextPlayer);
    }

    @Override
    public boolean isLegalMove(Position position, Position t1) {
        if(isPositionOufOfBounds(position) || isPositionOufOfBounds(t1)){
            return  false;
        }

        Player from = board[position.col()][position.row()];
        Player to = board[t1.col()][t1.row()];

        if(from!=nextPlayer){
            return false;
        }

        int rowMove = Math.abs(position.row() -  t1.row());
        int colMove = Math.abs(position.col() -  t1.col());

        Logger.trace(String.format("From: %d,%d to: %d,%d \n Distance vector: (%d,%d)",
                position.row(), position.col(),
                t1.row(), t1.col(),
                rowMove, colMove));

        if(colMove>1 || rowMove>1 || colMove*rowMove==1){return false;}

        if(( from == Player.PLAYER_1 && to == Player.PLAYER_2)){
            return true;
        }

        return from == Player.PLAYER_2 && to == null;
    }


    @Override
    public void makeMove(Position position, Position t1) {
        if(isPositionOufOfBounds(position) || isPositionOufOfBounds(t1)){
            return;
        }

        if(nextPlayer==Player.PLAYER_1){
            player1Positions.remove(position);
            player1Positions.add(t1);
            player2Positions.remove(t1);
        }
        else{
            player2Positions.remove(position);
            player2Positions.add(t1);
        }


        board[position.col()][position.row()]=null;
        board[t1.col()][t1.row()] = nextPlayer;

        Move currentMove = new Move(position, t1, getNextPlayer());
        previousMoves.add(currentMove);

        switchPlayer();
    }

    public void undoLastMove(){

        if (previousMoves.isEmpty()) {
            throw new NoSuchElementException("No moves to undo.");
        }

        Move lastMove = previousMoves.getLast();
        Position position = lastMove.getFromPosition();
        Position t1 = lastMove.getToPosition();

        if(lastMove.getTeam()==Player.PLAYER_1){
            player1Positions.remove(t1);
            player1Positions.add(position);
            player2Positions.add(t1);
            board[t1.col()][t1.row()]=Player.PLAYER_2;
        }
        else{
            player2Positions.remove(t1);
            player2Positions.add(position);
            board[t1.col()][t1.row()]=null;
        }

        board[position.col()][position.row()] = lastMove.getTeam();

        previousMoves.removeLast();

        switchPlayer();

    }

    private boolean isNotStranded(Position position, Player player){
        boolean isStranded = false;
        List<Position> availableMoves = new ArrayList<>();
        currentAvailableMoves = new ArrayList<>();
        currentAvailableMoves.addAll(availableMoves);

        availableMoves.add(new Position(position.row()-1, position.col()));
        availableMoves.add(new Position(position.row()+1, position.col()));
        availableMoves.add(new Position(position.row(), position.col()-1));
        availableMoves.add(new Position(position.row(), position.col()+1));

        Player target = player == Player.PLAYER_1 ? Player.PLAYER_2 : null;

        for (Position position_: availableMoves) {

            if(isPositionOufOfBounds(position_)){
                continue;
            }
            if(board[position_.col()][position_.row()]==target){
                currentAvailableMoves.add(position_);
                isStranded = true;
            }
        }
        return isStranded;
    }


    private boolean isBlueWinning(){
        for (Position position_: player1Positions) {
            if(isNotStranded(position_, Player.PLAYER_1)){
                return false;
            }
        }
        return true;
    }

    private boolean isRedWinning(){
        int row = player1Positions.get(0).row();
        int col = player1Positions.get(0).col();
        boolean oneRow = true;
        boolean oneCol = true;

        for (Position pos: player1Positions) {
            if(pos.row()!= row){
                oneRow = false;
            }
            if(pos.col() != col){
                oneCol = false;
            }
        }
        return (oneCol||oneRow);
    }


    @Override
    public boolean isGameOver() {
        return (isBlueWinning() || isRedWinning());
    }

    @Override
    public Status getStatus() {
        if (!isGameOver()) {
            return Status.IN_PROGRESS;
        }
        if(isRedWinning() && isBlueWinning()){
            return  Status.DRAW;
        }
        if(isBlueWinning()){
            return Status.PLAYER_1_WINS;
        }
        else{
            return Status.PLAYER_2_WINS;
        }
    }
}
