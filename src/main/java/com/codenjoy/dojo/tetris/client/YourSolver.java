package com.codenjoy.dojo.tetris.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.client.AbstractJsonSolver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.RandomDice;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * User: your name
 * Это твой алгоритм AI для игры. Реализуй его на свое усмотрение.
 * Обрати внимание на {@see YourSolverTest} - там приготовлен тестовый
 * фреймворк для тебя.
 */
public class YourSolver extends AbstractJsonSolver<Board> {

    private Dice dice;

    private BestMoveFinder finder;

    public YourSolver(Dice dice , BestMoveFinder finder) {
        this.dice = dice;
        this.finder = finder;
    }

    @Override
    public String getAnswer(Board board) {
        finder.setBoard(board);
        return finder.findBestMove();
    }

    public static void main(String[] args) {
        BestMoveFinder finder = new BestMoveFinder();
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                "http://codebattle2020.westeurope.cloudapp.azure.com/codenjoy-contest/board/player/rtjg3a9drjo97krsggut?code=1644343969008355722",
                new YourSolver(new RandomDice(), finder),
                new Board());
    }

}
