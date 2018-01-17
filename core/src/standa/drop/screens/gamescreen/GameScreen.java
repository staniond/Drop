package standa.drop.screens.gamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import standa.drop.BackgroundRainManager;
import standa.drop.DropGame;
import standa.drop.entitites.Bucket;
import standa.drop.screens.InfoScreen;

public class GameScreen implements Screen{

    private final DropGame game;
    public int width, height, tickCount;

    private OrthographicCamera camera;
    private SpriteBatch batch;

    public Bucket bucket;
    private DropManager dropManager;
    private standa.drop.BackgroundRainManager rainManager;
    private InfoPrinter printer;

    private int health = 3, score = 0, highScore = 0;

    //TODO poolable managers (drops, raindrops)

    public GameScreen(DropGame game){
        this.game = game;
        width = game.width;
        height = game.height;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
        batch = game.batch;

        Texture bucketTexture = new Texture(Gdx.files.internal("sprites/bucket.png"));
        bucket = new Bucket(width / 2, height / 20, bucketTexture.getWidth(), bucketTexture.getHeight(), bucketTexture, this);

        Texture dropTexture = new Texture(Gdx.files.internal("sprites/droplet.png"));
        Sound dropSound = Gdx.audio.newSound(Gdx.files.internal("sounds/dropSound.wav"));
        Sound gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sounds/gameover.wav"));
        dropManager = new DropManager(this, dropTexture, dropSound, gameOverSound);

        Texture rainDropTexture = new Texture(Gdx.files.internal("sprites/raindrop.png"));
        rainManager = new BackgroundRainManager(rainDropTexture, game);

        printer = new InfoPrinter(game);

        highScore = game.pref.getInteger("highScore");
    }


    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        tick();

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0, 0, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        draw();
        batch.end();
    }

    public void updateHighScore(int score){
        if(score > highScore){
            highScore = score;
            game.pref.putInteger("highScore", highScore);
            game.pref.flush();
        }
    }

    private void draw() {
        rainManager.draw(batch);
        bucket.draw(batch);
        dropManager.draw(batch);
        printer.draw(batch);
    }

    private void tick() {
        bucket.tick();
        dropManager.tick();
        rainManager.tick();
        updateHighScore(score);
        printer.setText(score, health, highScore);
        if(health == 0) game.setScreen(new InfoScreen(game, "Game over"));
        tickCount++;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        game.setScreen(new InfoScreen(game, "Game paused", this));
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        bucket.dispose();
        dropManager.dispose();
        rainManager.dispose();
    }

    public void removeHealth(){
        health--;
    }

    public void addScore(){
        score++;
    }
}