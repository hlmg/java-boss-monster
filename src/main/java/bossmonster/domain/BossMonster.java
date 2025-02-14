package bossmonster.domain;

import java.util.Random;

public class BossMonster {
    public static final String INVALID_HP_MESSAGE = "보스 몬스터의 HP는 100이상 300이하여야 합니다.";
    private static final Random random = new Random(System.currentTimeMillis());
    private static final int MIN_HP = 100;
    private static final int MAX_HP = 300;
    public static final int MIN_DAMAGE = 0;
    public static final int MAX_DAMAGE = 20;
    public static final int DEAD = 0;
    private final Energy hp;
    private int attackedCount;

    public BossMonster(int hp) {
        validateHp(hp);
        this.hp = new Energy(hp);
        attackedCount = 0;
    }

    private void validateHp(int hp) {
        if (hp < MIN_HP || MAX_HP < hp) {
            throw new IllegalArgumentException(INVALID_HP_MESSAGE);
        }
    }

    public Energy getHp() {
        return hp;
    }

    public void decreaseHp(int attackDamage) {
        hp.change(-attackDamage);
        attackedCount++;
    }

    public void attack(Player player) {
        player.decreaseHp(getDamage());
    }

    private int getDamage() {
        return random.nextInt(MIN_DAMAGE, MAX_DAMAGE + 1);
    }

    public boolean isDead() {
        return hp.getCurrentEnergy() == DEAD;
    }

    public int getAttackedCount() {
        return attackedCount;
    }
}
