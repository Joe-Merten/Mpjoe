package de.jme.mpjoe.android;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

public class Mpjoe extends Activity {
    static {
        ch.qos.logback.classic.android.BasicLogcatConfigurator.configureDefaultContext();
        // Logback Loglevel auf TRACE, weil ist per Default auf DEBUG
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(ch.qos.logback.classic.Level.TRACE);
    }

    static final Logger logger = LoggerFactory.getLogger(Mpjoe.class);

    TableLayout table;
    Button redButton;
    Button greenButton;
    Button blueButton;
    Button blackButton;
    Button whiteButton;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger.info("=== Initialing ===");
        setContentView(R.layout.main);

        // get all the view components
        table = (TableLayout)findViewById(R.id.Table);
        redButton   = (Button)findViewById(R.id.ButtonRed);
        greenButton = (Button)findViewById(R.id.ButtonGreen);
        blueButton  = (Button)findViewById(R.id.ButtonBlue);
        blackButton = (Button)findViewById(R.id.ButtonBlack);
        whiteButton = (Button)findViewById(R.id.ButtonWhite);

        // default the full screen to white
        table.setBackgroundColor(Color.WHITE);

        // hook up all the buttons with a table color change on click listener
        redButton.setOnClickListener  (onClickChangeColor(Color.RED  ));
        greenButton.setOnClickListener(onClickChangeColor(Color.GREEN));
        blueButton.setOnClickListener (onClickChangeColor(Color.BLUE ));
        blackButton.setOnClickListener(onClickChangeColor(Color.BLACK));
        whiteButton.setOnClickListener(onClickChangeColor(Color.WHITE));

        logger.info("=== Initialized ===");
    }

    View.OnClickListener onClickChangeColor(final int color) {
        return new View.OnClickListener() {
            public void onClick(View view) {
                logger.error("slf4j === onClick error ===");
                logger.warn ("slf4j === onClick warn ===");
                logger.info ("slf4j === onClick info ===");
                logger.debug("slf4j === onClick debug ===");
                logger.trace("slf4j === onClick trace ===");

                table.setBackgroundColor(color);
            }
        };
    }
}
