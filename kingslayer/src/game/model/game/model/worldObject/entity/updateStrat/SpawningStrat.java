package game.model.game.model.worldObject.entity.updateStrat;

import game.message.toServer.MakeEntityRequest;
import game.model.game.model.GameModel;
import game.model.game.model.team.Team;
import game.model.game.model.team.TeamResourceData;
import game.model.game.model.worldObject.entity.Entity;
import game.model.game.model.worldObject.entity.entities.Minions;
import javafx.util.Pair;

import java.util.function.Function;

public class SpawningStrat extends UpdateStrat {
  int counter;
  int maxSpawns;
  int spawnCounter;
  int timeBetweenSpawns;
  Function<Pair<Double, Double>, Entity> function;

  public SpawningStrat() {

  }

  public SpawningStrat(int timeBetweenSpawns, int maxSpawns, Function<Pair<Double, Double>, Entity> function) {
    this.counter = 0;
    this.timeBetweenSpawns = timeBetweenSpawns;
    this.maxSpawns = maxSpawns;
    this.spawnCounter = 0;
    this.function = function;
  }

  @Override
  protected void update(Entity entity, GameModel model, double seconds) {
    counter++;
    if (counter > timeBetweenSpawns) {
      if (spawnCounter < maxSpawns) {
        model.processMessage(new MakeEntityRequest(function.apply(new Pair(entity.data.x, entity.data.y))));
        spawnCounter++;
      }
      counter = 0;
    }
  }

  @Override
  public void upgrade(GameModel model) {
    this.maxSpawns += 10;
  }
}