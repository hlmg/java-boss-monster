package bossmonster.domain;

public class Player {
    public static final String INVALID_NAME_MASSAGE = "플레이어의 이름은 1글자 이상 5글자 이하여야 합니다.";
    public static final String INVALID_STAT_MESSAGE = "플레이어의 HP와 MP의 합은 200이어야 합니다.";
    public static final String INVALID_HP_MESSAGE = "hp는 1 이상이어야 합니다.";
    public static final String INVALID_MP_MESSAGE = "mp는 0 이상이어야 합니다.";
    private static final int TOTAL_STAT = 200;
    private static final int MAX_NAME_LENGTH = 5;
    private static final int MIN_HP = 1;
    private static final int MIN_MP = 0;
    public static final int DEAD = 0;
    public static final String NOT_ENOUGH_MANA = "마나가 부족합니다.";
    private final String name;
    private final Energy hp;
    private final Energy mp;

    public Player(String name, int hp, int mp) {
        validateName(name);
        validateHp(hp);
        validateMp(mp);
        validateTotalStat(hp, mp);
        this.name = name;
        this.hp = new Energy(hp);
        this.mp = new Energy(mp);
    }

    public void attack(BossMonster bossMonster, AttackType attackType) {
        checkEnoughMp(attackType);
        bossMonster.decreaseHp(attackType.getDamage());
        mp.change(attackType.getMpChange());
    }

    private void checkEnoughMp(AttackType attackType) {
        if (mp.isNotEnoughEnergy(attackType.getMpChange())){
            throw new IllegalArgumentException(NOT_ENOUGH_MANA);
        }
    }

    private void validateName(String name) {
        if (name.isBlank() || name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(INVALID_NAME_MASSAGE);
        }
    }

    private void validateHp(int hp) {
        if (hp < MIN_HP) {
            throw new IllegalArgumentException(INVALID_HP_MESSAGE);
        }
    }

    private void validateMp(int mp) {
        if (mp < MIN_MP) {
            throw new IllegalArgumentException(INVALID_MP_MESSAGE);
        }
    }

    private void validateTotalStat(int hp, int mp) {
        if (hp + mp != TOTAL_STAT) {
            throw new IllegalArgumentException(INVALID_STAT_MESSAGE);
        }
    }

    public String getName() {
        return name;
    }

    public Energy getHp() {
        return hp;
    }

    public Energy getMp() {
        return mp;
    }

    public void decreaseHp(int amount) {
        hp.change(-amount);
    }

    public boolean isDead() {
        return hp.getCurrentEnergy() == DEAD;
    }
}
