package com.github.amarcinkowski.metro.command;

import lombok.Builder;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Builder
@Setter
@ToString
class KeyInput {
    boolean alt = false;
    boolean ctrl = false;
    boolean shift = false;
    String keyCode;
}

@Slf4j
public class KeyboardCommand extends Command {


    public KeyboardCommand(Vector<String> args, HashMap<String, String> typedArgs) {
        super(args, typedArgs);
    }

    private String getCode(String txt) {
        return "VK_" + txt.toUpperCase();
    }

    private KeyInput keyTextToInput(String text) {
        // remove [ and ] from start and end
        String keyText = text.substring(1, text.length() - 1);
        boolean ctrl = false;
        boolean alt = false;
        if (keyText.contains("alt-")) {
            alt = true;
            keyText = keyText.replace("alt-", "");
        }
        if (keyText.contains("ctrl-")) {
            ctrl = true;
            keyText = keyText.replace("ctrl-", "");
        }
        return KeyInput.builder().alt(alt).ctrl(ctrl).keyCode(getCode(keyText)).build();
    }

    private Vector getKeys(String keys) {

        String keysLeft = keys;
        Vector<KeyInput> keyboard = new Vector();
        Vector<KeyInput> input = new Vector<>();
        Matcher matcher = Pattern.compile("\\[[a-z-]+\\]").matcher(keys);
        while (matcher.find()) {
            keysLeft = keysLeft.replaceFirst(Pattern.quote(matcher.group()), "\u0357"); // 0357: RIGHT HALF RING
            input.add(keyTextToInput(matcher.group()));

            log.debug("KeysLEFT:" + keysLeft + " FOUND: " + matcher.group());
        }
        for (char c : keysLeft.toCharArray()) {
            if (!new String("" + c).matches("[A-Za-z0-9\u0357 ]")) {
                log.debug("Char " + c + " is not ascii char.");
                continue;
            }
            KeyInput letter;
            if (c == '\u0357') {
                letter = input.remove(0);
            } else if (c == ' ') {
                letter = KeyInput.builder().keyCode(getCode("SPACE")).build();
            } else {
                letter = KeyInput.builder().shift(Character.isUpperCase(c)).keyCode(getCode(Character.toString(c))).build();
            }

            keyboard.add(letter);
            log.debug("CHAR:" + letter + " > " + c);
        }
        return keyboard;
    }

    @Override
    public String execute() {
        String args = getArgs().stream().collect(Collectors.joining(""));
        log.debug("EXEC keyboard # " + args);

        Vector<KeyInput> keys = getKeys(args);
        try {
            Robot robot = new Robot();
            robot.delay(50);
            for (KeyInput ki : keys) {
                int key = KeyEvent.class.getField(ki.keyCode).getInt(null);
                robot.keyPress(key);
                robot.keyRelease(key);
            }
        } catch (AWTException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return "Num of chars typed: " + keys.size();
    }
}
