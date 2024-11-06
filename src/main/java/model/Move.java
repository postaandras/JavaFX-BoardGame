package model;

import game.State;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Move {
    Position fromPosition;
    Position toPosition;
    State.Player team;
}
