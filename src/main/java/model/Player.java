package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Player {
    private String name;
    private int score;

    public void increaseScore(){
        score++;
    }

    @Override
    public String toString(){
        return String.format("%s, score:\t %d", name, score);
    }
}
