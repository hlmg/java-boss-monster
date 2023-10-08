package bossmonster.domain;

import bossmonster.exception.GameEndException;
import bossmonster.view.GuideText;
import bossmonster.view.InputProcessor;
import bossmonster.view.OutputProcessor;

import java.util.Random;

import static bossmonster.domain.AttackType.MAGICK;
import static bossmonster.domain.AttackType.NORMAL;
import static bossmonster.view.GuideText.*;

public class BasicGameEngine implements GameEngine {
    private int turnCount = 0;
    private Player player;
    private Boss boss;
    private final InputProcessor inputProcessor;
    private final OutputProcessor outputProcessor;
    private final RuleChecker ruleChecker;

    public BasicGameEngine(InputProcessor inputProcessor, OutputProcessor outputProcessor, RuleChecker ruleChecker) {
        this.inputProcessor = inputProcessor;
        this.outputProcessor = outputProcessor;
        this.ruleChecker = ruleChecker;
    }

    @Override
    public void initGame() {
        boss = initBoss();
        player = initPlayer();
        startGame();
    }

    private Boss initBoss(){
        outputProcessor.print(BOSS_HP_INPUT);
        int bossHp = 0;
        boolean firstTimeInput = true;
        while (!ruleChecker.isAllowedBossInitHp(bossHp)){
            if(!firstTimeInput){
                outputProcessor.printError("보스의 HP는 100 이상 300 이하여야 합니다.");
                outputProcessor.print(String.format("\n%s", BOSS_HP_INPUT));
            }
            bossHp = inputProcessor.getInt();
            firstTimeInput = false;
        }
        return new Boss(bossHp);
    }

    private Player initPlayer(){
        String playerName = initPlayerName();
        String[] split = initPlayerHPAndMP().split(",");
        int hp = Integer.parseInt(split[0]);
        int mp = Integer.parseInt(split[1]);
        return new Player(playerName, hp, mp);
    }

    private String initPlayerName(){
        outputProcessor.print(PLAYER_NAME_INPUT);
        String name = "";
        boolean firstTimeInput = true;
        while (!ruleChecker.isAllowedPlayerName(name)){
            if(!firstTimeInput){
                outputProcessor.printError("플레이어의 이름은 5글자 이하여야 합니다.");
                outputProcessor.print(PLAYER_NAME_INPUT);
            }
            name = inputProcessor.getString();
            firstTimeInput = false;
        }
        return name;
    }

    private String initPlayerHPAndMP(){
        outputProcessor.print(PLAYER_HP_MP_INPUT);
        String hpAndMP = "";
        boolean firstTimeInput = true;
        while (!ruleChecker.isAllowedPlayerHpAndMP(hpAndMP)){
            if(!firstTimeInput){
                outputProcessor.printError("플레이어의 HP와 MP의 합은 200이어야합니다.");
                outputProcessor.print(PLAYER_HP_MP_INPUT);
            }
            hpAndMP = inputProcessor.getString();
            firstTimeInput = false;
        }
        return hpAndMP;
    }

    private void startGame() {
        if (!canStart()){
            return;
        }
        printInitStatus();
        try {
            playerTurn();
        }catch (GameEndException e){
            outputProcessor.print(String.format(KILL_BOSS, player.getName(), turnCount));
        }
    }

    private boolean canStart(){
        return player != null && boss != null;
    }

    private void printInitStatus(){
        outputProcessor.print(GAME_START);
        outputProcessor.print(NEW_LINE_DOUBLE_LINE);
        outputProcessor.print(boss);
        outputProcessor.print(BOSS_INIT_ICON);
        outputProcessor.print(player);
        outputProcessor.print(DOUBLE_LINE_NEW_LINE);
    }

    private void playerTurn(){
        turnCount++;
        if(!boss.isNew()){
            printStatus();
        }
        outputProcessor.print(ATTACK_STRATEGY);
        AttackType attackType = AttackType.fromInt(inputProcessor.getInt());
        if(attackType == NORMAL){
            playerAttack();
        }
        if(attackType == MAGICK){
            tryPlayerMagicAttack();
        }
        try {
            bossTurn();
        }catch (GameEndException e){
            printBossWinStatus();
        }
    }

    private void printStatus(){
        outputProcessor.print(NEW_LINE_DOUBLE_LINE);
        outputProcessor.print(boss);
        outputProcessor.print(BOSS_HIT_ICON);
        outputProcessor.print(player);
        outputProcessor.print(DOUBLE_LINE_NEW_LINE);
    }

    private void playerAttack(){
        outputProcessor.print(String.format(GuideText.NORMAL_ATTACK, NORMAL.getDamageValue()));
        player.attack(boss, NORMAL.getDamageValue());
    }

    private void tryPlayerMagicAttack(){
        boolean attackAble = true;
        if(ruleChecker.canPlayerMagicAttack(player, MAGICK.getUseMpValue())) {
            playerMagicAttack();
            attackAble = false;
        }
        if(attackAble){
            outputProcessor.print(FORCE_NORMAL_ATTACK);
            playerAttack();
        }
    }

    private void playerMagicAttack(){
        outputProcessor.print(String.format(GuideText.MAGIK_ATTACK, MAGICK.getDamageValue()));
        player.magicAttack(boss, MAGICK.getDamageValue());
    }

    private void bossTurn(){
        int damage = new Random().nextInt(21);
        outputProcessor.print(String.format(BOSS_ATTACK, damage));
        boss.attack(player, damage);
        try {
            playerTurn();
        }catch (GameEndException e){
            outputProcessor.print(String.format(KILL_BOSS, player.getName(), turnCount));
        }
    }

    private void printBossWinStatus(){
        outputProcessor.print(NEW_LINE_DOUBLE_LINE);
        outputProcessor.print(boss);
        outputProcessor.print(BOSS_WIN_ICON);
        outputProcessor.print(player);
        outputProcessor.print(DOUBLE_LINE_NEW_LINE);
        outputProcessor.print(String.format(KILL_PLAYER, player.getName()));
    }
}
