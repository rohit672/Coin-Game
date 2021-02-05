package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class marioGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background ;
	Texture[] man ;
	Texture dizzy ;
	int pause = 0 ;
	int manState = 0 ;
	float velocity = 0 ;
	float gravity = 0.3f ;
	int manY = 0 ;
	int score = 0 ;

	int gameState = 0 ;

	BitmapFont font ;
	BitmapFont gameLost ;


	Rectangle manRectangle ;

	ArrayList<Integer> coinX = new ArrayList<Integer>() ;
	ArrayList<Integer> coinY = new ArrayList<Integer>() ;
	ArrayList<Rectangle> coinRectangle = new ArrayList<Rectangle>() ;

	ArrayList<Integer> bombX = new ArrayList<Integer>() ;
	ArrayList<Integer> bombY = new ArrayList<Integer>() ;
	ArrayList<Rectangle> bombRectangle = new ArrayList<Rectangle>() ;

	int bombcount = 0 ;
	int coinCount = 0 ;
    Random random ;
	Texture coin ;
	Texture bomb ;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png") ;
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		dizzy = new Texture("dizzy-1.png") ;

		manY = Gdx.graphics.getHeight()/2 ;

		coin = new Texture("coin.png") ;
		bomb = new Texture("bomb.png") ;
		//manRectangle = new Rectangle() ;
		random = new Random() ;

		font = new BitmapFont() ;
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		gameLost = new BitmapFont() ;
		gameLost.setColor(Color.BLUE);
		gameLost.getData().setScale(10);


	}

	public void makeCoin() {
		    float height = random.nextFloat() * Gdx.graphics.getHeight() ;
		    coinY.add((int)height) ;
		    coinX.add(Gdx.graphics.getWidth()) ;
	}

	public void makeBomb() {

		float height = random.nextFloat() * Gdx.graphics.getHeight() ;
		bombY.add((int)height) ;
		bombX.add(Gdx.graphics.getWidth()) ;
	}

	@Override
	public void render () {
           batch.begin();


           batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

           if (gameState == 1) {

			   if (coinCount < 100) {
				   coinCount ++ ;
			   } else  {
				   coinCount = 0 ;
				   makeCoin();
			   }

			   if (bombcount < 250) {
				   bombcount ++ ;
			   }

			   else {
				   bombcount = 0 ;
				   makeBomb();
			   }

			   coinRectangle.clear();
			   for(int i = 0 ; i < coinX.size() ; i ++ ) {
				   batch.draw(coin, coinX.get(i), coinY.get(i));
				   coinX.set(i,coinX.get(i) - 5) ;
				   //if (coinX.get(i) < 0 ) coinX.remove(i) ;
				   coinRectangle.add(new Rectangle(coinX.get(i) , coinY.get(i) , coin.getWidth() , coin.getHeight())) ;
			   }

			   bombRectangle.clear();
			   for (int  i = 0 ; i < bombX.size() ; i ++ ) {

				   batch.draw(bomb, bombX.get(i), bombY.get(i));
				   bombX.set(i, bombX.get(i) - 8) ;
				   bombRectangle.add(new Rectangle(bombX.get(i) , bombY.get(i) , bomb.getWidth() , bomb.getHeight())) ;
			   }

			   if (Gdx.input.justTouched() ) {

				   velocity = -15 ;
			   }

			   if (pause < 8) pause ++ ;
			   else {

				   pause = 0 ;
				   if (manState >= 3) {
					   manState = 0;
				   } else manState++;
			   }

			   velocity += gravity ;
			   manY -= velocity ;

			   if (manY <= 1 ) {

				   manY = 1 ;

			   }
		   } else if (gameState == 0 ) {

           	     if (Gdx.input.justTouched()) {
           	     	  gameState = 1 ;
				 }

		   }else if (gameState == 2 ) {
			   if (Gdx.input.justTouched()) {
				   gameState = 1 ;
				   manY = Gdx.graphics.getHeight()/2 ;
				   velocity = 0 ;
				   score = 0 ;
				   coinY.clear();
				   coinX.clear();
				   bombX.clear();
				   bombY.clear();
				   coinRectangle.clear();
				   bombRectangle.clear();
				   coinCount = 0;
				   bombcount = 0 ;

			   }
		   }

           if (gameState == 2) {
                     batch.draw(dizzy,Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
                     gameLost.draw(batch,"GAME LOST",150,1000);
			         font.draw(batch , String.valueOf(score) , 500 , 750 ) ;

		   }else {
			   batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		   }
           manRectangle = new Rectangle(Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2 , manY , man[manState].getWidth() , man[manState].getHeight());

           for (int  i = 0 ; i < coinRectangle.size() ; i ++ ) {

           	       if (Intersector.overlaps(manRectangle, coinRectangle.get(i))) {
           	       	     Gdx.app.log("coin" , "collision!!!!");
           	       	     score ++ ;

           	       	     coinRectangle.remove(i) ;
           	       	     coinX.remove(i) ;
           	       	     coinY.remove(i) ;

           	       	     break;
				   }
		   }

		for (int  i = 0 ; i < bombRectangle.size() ; i ++ ) {

			if (Intersector.overlaps(manRectangle, bombRectangle.get(i))) {
				Gdx.app.log("bomb" , "collision!!!!");
				gameState = 2 ;
				break;
			}
		}

           if (gameState == 1 )
           font.draw(batch , String.valueOf(score) , 100 , 200 ) ;
           batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}

