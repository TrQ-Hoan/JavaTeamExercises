package util;

import java.util.HashMap;

public class VNCharacter {

    private static final HashMap<Character, Character> vi2en = new HashMap<Character, Character>() {
        {
            put('À', 'A'); put('Á', 'A'); put('Â', 'A'); put('Ã', 'A');
            put('È', 'E'); put('É', 'E'); put('Ê', 'E'); put('Ì', 'I');
            put('Í', 'I'); put('Ò', 'O'); put('Ó', 'O'); put('Ô', 'O');
            put('Õ', 'O'); put('Ù', 'U'); put('Ú', 'U'); put('Ý', 'Y');
            put('à', 'a'); put('á', 'a'); put('â', 'a'); put('ã', 'a');
            put('è', 'e'); put('é', 'e'); put('ê', 'e'); put('ì', 'i');
            put('í', 'i'); put('ò', 'o'); put('ó', 'o'); put('ô', 'o');
            put('õ', 'o'); put('ù', 'u'); put('ú', 'u'); put('ý', 'y');
            put('Ă', 'A'); put('ă', 'a'); put('Đ', 'D'); put('đ', 'd'); 
            put('Ĩ', 'I'); put('ĩ', 'i'); put('Ũ', 'U'); put('ũ', 'u'); 
            put('Ơ', 'O'); put('ơ', 'o'); put('Ư', 'U'); put('ư', 'u'); 
            put('Ạ', 'A'); put('ạ', 'a'); put('Ả', 'A'); put('ả', 'a'); 
            put('Ấ', 'A'); put('ấ', 'a'); put('Ầ', 'A'); put('ầ', 'a');
            put('Ẩ', 'A'); put('ẩ', 'a'); put('Ẫ', 'A'); put('ẫ', 'a');
            put('Ậ', 'A'); put('ậ', 'a'); put('Ắ', 'A'); put('ắ', 'a');
            put('Ằ', 'A'); put('ằ', 'a'); put('Ẳ', 'A'); put('ẳ', 'a');
            put('Ẵ', 'A'); put('ẵ', 'a'); put('Ặ', 'A'); put('ặ', 'a');
            put('Ẹ', 'E'); put('ẹ', 'e'); put('Ẻ', 'E'); put('ẻ', 'e');
            put('Ẽ', 'E'); put('ẽ', 'e'); put('Ế', 'E'); put('ế', 'e');
            put('Ề', 'E'); put('ề', 'e'); put('Ể', 'E'); put('ể', 'e');
            put('Ễ', 'E'); put('ễ', 'e'); put('Ệ', 'E'); put('ệ', 'e');
            put('Ỉ', 'I'); put('ỉ', 'i'); put('Ị', 'I'); put('ị', 'i');
            put('Ọ', 'O'); put('ọ', 'o'); put('Ỏ', 'O'); put('ỏ', 'o');
            put('Ố', 'O'); put('ố', 'o'); put('Ồ', 'O'); put('ồ', 'o');
            put('Ổ', 'O'); put('ổ', 'o'); put('Ỗ', 'O'); put('ỗ', 'o');
            put('Ộ', 'O'); put('ộ', 'o'); put('Ớ', 'O'); put('ớ', 'o');
            put('Ờ', 'O'); put('ờ', 'o'); put('Ở', 'O'); put('ở', 'o');
            put('Ỡ', 'O'); put('ỡ', 'o'); put('Ợ', 'O'); put('ợ', 'o');
            put('Ụ', 'U'); put('ụ', 'u'); put('Ủ', 'U'); put('ủ', 'u');
            put('Ứ', 'U'); put('ứ', 'u'); put('Ừ', 'U'); put('ừ', 'u');
            put('Ử', 'U'); put('ử', 'u'); put('Ữ', 'U'); put('ữ', 'u');
            put('Ự', 'U'); put('ự', 'u');
        }
    };

    public static char removeAccent(char ch) {
        if (vi2en.containsKey(ch)) return vi2en.get(ch);
        return ch;
    }

    public static String removeAccent(String str) {
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < sb.length(); i++) {
            sb.setCharAt(i, removeAccent(sb.charAt(i)));
        }
        return sb.toString();
    }
}
