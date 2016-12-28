package br.com.katalog.katalogweb.utils;

import android.util.Log;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by luciano on 04/09/2016.
 */
public class StringUtils {

    public static String removeDiacritics(String word){
//        String str = new String(word);
        return Normalizer.normalize(word.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }


    public static String alphabeticTitle(String s){
        String resultado = s;
        //expressão para localizar titulos iniciados por [a, o , um e the]
        Pattern p = Pattern.compile("^[aáàoó][sn]?\\s|^uma?\\s|^the\\s", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(s.trim());
        if (m.find()){
            String moveToEnd = ", " + m.group().trim();
            resultado = m.replaceAll("") + moveToEnd;
        }
//        Log.i("utils", "aphabetic result: " + resultado);
        return resultado;
    }

    public static String normalizeTitle(String s){
        String result = s;
        Pattern p = Pattern.compile(",\\s[aáàoó][sn]?\\z|,\\suma?\\z|,\\sthe\\z", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(s.trim());
        if (m.find()){
            String moveToStart = m.group().replaceAll(", ", "") + " ";
            result = moveToStart + m.replaceAll("");
        }
//        Log.i("utils", "normalize result: " + result);
        return result;
    }
}
