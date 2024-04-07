
package com.mygme.test;



import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class DropGame extends Game {



	private Texture img;
	private Texture img2;
	private Texture img3;
	private Texture img4;
	private Texture img5;
	private Sound dropSound;
	private Music rainMusic;
	private Sound explosion;
	public SpriteBatch batch;
	private OrthographicCamera camera;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private Array<Rectangle> metors;
	private long lastDropTime;
	private int score;
	private String yourScoreName;
	BitmapFont yourBitMapFontName;
	private long mlastDropTime;



	@Override
	public void create() {


		score = 0;
		yourScoreName = "Score: 0";
		yourBitMapFontName = new BitmapFont();

		batch = new SpriteBatch();

		img2 = new Texture("drop.png");
		img3 = new Texture("bucket.png");
		img4 = new Texture("metor.png");
		img5 = new Texture("win.png");

		dropSound = Gdx.audio.newSound(Gdx.files.internal("waterDrop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("treeRain.mp3"));
		explosion = Gdx.audio.newSound(Gdx.files.internal("exp1.wav"));

		//use sound if shorter than 10 sec and use music for longer audio

		rainMusic.setLooping(true);
		rainMusic.play();


		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);


		batch = new SpriteBatch();

		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2;
		bucket.y = 20;
		bucket.width = 24;
		bucket.height = 64;


		raindrops = new Array<Rectangle>();
		spawnRaindrop();

		metors = new Array<Rectangle>();
		spawnMetors();

	}



	@Override
	public void render() {


		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//Render the current Screen
		super.render();


		ScreenUtils.clear(0, 0, 0.2f, 1); //rgb alpha in range [0,1]

		camera.update();


		//now we render our bucket
		batch.setProjectionMatrix(camera.combined);



		batch.begin();
		batch.draw(img3, bucket.x, bucket.y);
		for (Rectangle raindrop : raindrops) {
			batch.draw(img2, raindrop.x, raindrop.y);

		}
		for(Rectangle metor: metors){
			batch.draw(img4,metor.x,metor.y,64,64);
		}
		if(score>=10) {
			batch.draw(img5, 800/2, 480/2,300,300);
			bucket.x = 800 / 2 - 64 / 2;
			bucket.y=20;
			metors.clear();
			yourScoreName = "WINNER, touch to continue";
			if(Gdx.input.isTouched())
				resetGame();
		}
		yourBitMapFontName.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		yourBitMapFontName.draw(batch,yourScoreName,25,470);
		batch.end();



		//move bucket below
		if (Gdx.input.isTouched())
		{
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);

			bucket.x = touchPos.x - 64 / 2;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();


		//need to make sure our bucket stays in screen
		if (bucket.x < 0) bucket.x = 0;
		if (bucket.x > 800 - 64) bucket.x = 800 - 64;


		if (TimeUtils.nanoTime() - lastDropTime > 1000000000) {
			spawnRaindrop();

		}
		if(TimeUtils.nanoTime() - mlastDropTime>300000000)
			spawnMetors();


		for(Iterator<Rectangle>iters = metors.iterator(); iters.hasNext();){
			Rectangle metor = iters.next();
			metor.y -= 200 * Gdx.graphics.getDeltaTime();
			if(metor.y +64<0)iters.remove();

			if(metor.overlaps(bucket)) {
				score--;
				yourScoreName = "Score: "+score;
				explosion.play();
				iters.remove();
			}
		}


		for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
				Rectangle raindrop = iter.next();
				raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
				if (raindrop.y + 64 < 0) iter.remove();

			if (raindrop.overlaps(bucket)) {
				score++;
				yourScoreName = "Score: "+score;
				dropSound.play();
				iter.remove();
			}


		}
		if(score<0){
			yourScoreName = "YOU LOSE!  Click anywhere to try again";
			bucket.x = 800 / 2 - 64 / 2;
			bucket.y=20;
		}


		if(Gdx.input.isTouched()){
			resetGame();
		}

	}

	private void resetGame(){
		score = 0;
		yourScoreName = "Score: 0";
		bucket.x = 800 / 2 - 64 / 2;
		bucket.y=20;


	}

	private void spawnRaindrop() {

		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800 - 64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();


	}

	private void spawnMetors(){
		Rectangle metor = new Rectangle();
		metor.x = MathUtils.random(0,800 - 64);
		metor.y = 480;
		metor.width = 64;
		metor.height = 64;
		metors.add(metor);
		mlastDropTime = TimeUtils.nanoTime();


	}




	@Override
	public void dispose() {
		batch.dispose();
		img.dispose();
		img2.dispose();
		img3.dispose();
		img4.dispose();
		img5.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		explosion.dispose();


	}
}


