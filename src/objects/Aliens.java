package objects;

import java.io.IOException;
import java.util.ArrayList;

import state.SetStage.Stage;
import effects.Effects;
import effects.SoundType;
import entities.ObjectType;

public class Aliens {

    private int totalNumberOfAliens;
    private int numberOfAliensRow;
    private int gap = 45;
    public static boolean isMovingRight = true;
    public static boolean isMovingDown = false;
    private long moveDownTime = 0;

    private ArrayList<Enemy> aliens = new ArrayList<Enemy>();

    public Aliens() {

    }

    public void createAliens(Stage stage) {

        aliens.clear();

        totalNumberOfAliens = stage.getNumberTotalOfAliens();
        numberOfAliensRow = stage.getNumberOfAliensRow();

        int row = -1;
        int cont = 0;
        
        for (int i = 0; i < totalNumberOfAliens; i++) {
            if (i % numberOfAliensRow == 0) {
                row++;
                cont = 0;
            }

            int rand = (int) (Math.random() * (5)); //[0 a 1]
            ObjectType objType;

            switch (rand + 1) {
            case 1:
                objType = ObjectType.ENEMY1;
                break;
            case 2:
                objType = ObjectType.ENEMY2;
                break;
            case 3:
                objType = ObjectType.ENEMY3;
                break;
            case 4:
                objType = ObjectType.ENEMY4;
                break;
            default:
                objType = ObjectType.ENEMY5;
                break;
            }

            Enemy enemy = new Enemy(0 + cont*gap, 32 + row*gap, 32, 32, stage.getSpeed(),
                    objType, stage.getBombSpeed(), stage.getHits());
            aliens.add(enemy);
            cont++;
        }
        
    }

    public void draw(long newTime) {
        for (Enemy enemy : aliens) {
            enemy.draw(newTime);
        }
    }

    public void updateTime(int delta, long systime) throws IOException {
        for (Enemy enemy : aliens) {
            enemy.updateTime(delta, systime);
        }
        
        if (isMovingDown)
            moveDown(systime);
    }

    private void moveDown(long systime) {
        if (moveDownTime == 0)
            moveDownTime = systime + 100;
        
        if (moveDownTime > systime)
            for (Enemy enemy : aliens) {
                enemy.setDX(0);
                enemy.setDY(enemy.getSpeed());
            }
        else {
            for (Enemy enemy : aliens) {
                enemy.setDY(0);
                enemy.setDX(enemy.getSpeed());
            }
            isMovingDown = false;
            moveDownTime = 0;
        }
    }

    public boolean collison(Player player, CollisionType collisionType) throws IOException {
        boolean collision = false;

        for (Enemy enemy : aliens) {
            switch(collisionType) {
            case ROCKET_X_ENEMY:
                if (player.bombIntersects(enemy)) {
                    enemy.hit();
                    if (!enemy.isMoving()) {
                        collision = true;
                        totalNumberOfAliens--;
                        
                        Effects.playSound(SoundType.EXPLOSION_ROCKET);
                    }
                    break;
                }
                break;
            case BOMB_X_PLAYER:
                if (enemy.bombIntersects(player)) {
                    collision = true;
                    
                    Effects.playSound(SoundType.EXPLOSION_BOMB);
                    
                    break;
                }
                break;
            case ENEMY_X_PLAYER:
                if (player.intersects(enemy)) {
                    enemy.hit();
                    if (!enemy.isMoving()) {
                        collision = true;
                        totalNumberOfAliens--;

                        Effects.playSound(SoundType.EXPLOSION_ROCKET);
                    }
                    
                    Effects.playSound(SoundType.EXPLOSION_BOMB);

                    break;
                }
                break;
            }
        }

        return collision;
    }

    public int getTotalNumberOfAliens() {
        return totalNumberOfAliens;
    }

    /*
     * Get the lowest enemy
     */
    public double getLowestEnemy() {
        double low = 0;
        
        for (Enemy enemy : aliens) {
            if (enemy.isMoving() && (enemy.getY() > low))
                low = enemy.getY();
        }
        
        return low;
    }
}
