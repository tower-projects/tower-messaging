/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.iamcyw.tower;

/**
 * Copy to jodd.util
 * <p>
 * Pool of <code>String</code> constants to prevent repeating of
 * hard-coded <code>String</code> literals in the code.
 * Due to fact that these are <code>public static final</code>
 * they will be inlined by java compiler and
 * reference to this class will be dropped.
 * There is <b>no</b> performance gain of using this pool.
 * Read: https://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.5
 * <ul>
 * <li>Literal strings within the same class in the same package represent references to the same <code>String</code>
 * object.</li>
 * <li>Literal strings within different classes in the same package represent references to the same
 * <code>String</code> object.</li>
 * <li>Literal strings within different classes in different packages likewise represent references to the same
 * <code>String</code> object.</li>
 * <li>Strings computed by constant expressions are computed at compile time and then treated as if they were
 * literals.</li>
 * <li>Strings computed by concatenation at run time are newly created and therefore distinct.</li>
 * </ul>
 */
public class StringPool {

    public static final String AMPERSAND = "&";

    public static final String AND = "and";

    public static final String AT = "@";

    public static final String ASTERISK = "*";

    public static final String STAR = ASTERISK;

    public static final String BACK_SLASH = "\\";

    public static final String COLON = ":";

    public static final String COMMA = ",";

    public static final String DASH = "-";

    public static final String DOLLAR = "$";

    public static final String DOT = ".";

    public static final String DOT_DOT = "..";

    public static final String DOT_CLASS = ".class";

    public static final String DOT_JAVA = ".java";

    public static final String DOT_XML = ".xml";

    public static final String EMPTY = "";

    public static final String EQUALS = "=";

    public static final String FALSE = "false";

    public static final String SLASH = "/";

    public static final String HASH = "#";

    public static final String HAT = "^";

    public static final String LEFT_BRACE = "{";

    public static final String LEFT_BRACKET = "(";

    public static final String LEFT_CHEV = "<";

    public static final String DOT_NEWLINE = ",\n";

    public static final String NEWLINE = "\n";

    public static final String N = "n";

    public static final String NO = "no";

    public static final String NULL = "null";

    public static final String OFF = "off";

    public static final String ON = "on";

    public static final String PERCENT = "%";

    public static final String PIPE = "|";

    public static final String PLUS = "+";

    public static final String QUESTION_MARK = "?";

    public static final String EXCLAMATION_MARK = "!";

    public static final String QUOTE = "\"";

    public static final String RETURN = "\r";

    public static final String TAB = "\t";

    public static final String RIGHT_BRACE = "}";

    public static final String RIGHT_BRACKET = ")";

    public static final String RIGHT_CHEV = ">";

    public static final String SEMICOLON = ";";

    public static final String SINGLE_QUOTE = "'";

    public static final String BACKTICK = "`";

    public static final String SPACE = " ";

    public static final String TILDA = "~";

    public static final String LEFT_SQ_BRACKET = "[";

    public static final String RIGHT_SQ_BRACKET = "]";

    public static final String TRUE = "true";

    public static final String UNDERSCORE = "_";

    public static final String UTF_8 = "UTF-8";

    public static final String US_ASCII = "US-ASCII";

    public static final String ISO_8859_1 = "ISO-8859-1";

    public static final String Y = "y";

    public static final String YES = "yes";

    public static final String ONE = "1";

    public static final String ZERO = "0";

    public static final String DOLLAR_LEFT_BRACE = "${";

    public static final String HASH_LEFT_BRACE = "#{";

    public static final String CRLF = "\r\n";

    public static final String HTML_NBSP = "&nbsp;";

    public static final String HTML_AMP = "&amp";

    public static final String HTML_QUOTE = "&quot;";

    public static final String HTML_LT = "&lt;";

    public static final String HTML_GT = "&gt;";

    public static final String[] EMPTY_ARRAY = new String[0];

    // ---------------------------------------------------------------- array

    public static final byte[] BYTES_NEW_LINE = StringPool.NEWLINE.getBytes();

    private StringPool() {
        throw new IllegalStateException("Utility class");
    }

}
