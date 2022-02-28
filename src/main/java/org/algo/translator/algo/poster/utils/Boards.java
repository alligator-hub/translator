package org.algo.translator.algo.poster.utils;

import org.algo.translator.algo.poster.enums.ButtonData;
import org.algo.translator.algo.poster.payload.PostButtonDto;
import org.algo.translator.algo.poster.payload.PostingConfigurationDto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Yormamatov Davronbek
 * @since 20.02.2022
 */

public class Boards {
    public static InlineKeyboardMarkup asyncMessageToMembersBoard(PostingConfigurationDto dto) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        List<InlineKeyboardButton> notifyRow = new ArrayList<>();
        List<InlineKeyboardButton> descRow = new ArrayList<>();
        List<InlineKeyboardButton> countRow = new ArrayList<>();
        List<InlineKeyboardButton> usernameRow = new ArrayList<>();
        List<InlineKeyboardButton> postIdRow = new ArrayList<>();
        List<InlineKeyboardButton> hasInlineKeyboardRow = new ArrayList<>();
        List<InlineKeyboardButton> hasHtmlTextRow = new ArrayList<>();
        List<InlineKeyboardButton> testRow = new ArrayList<>();

        InlineKeyboardButton notification = new InlineKeyboardButton();
        notification.setText(ButtonData.SWITCH_NOTIFY.getText() + (dto.isNotification() ? "✅" : "❌"));
        notification.setCallbackData(ButtonData.SWITCH_NOTIFY.getData());
        notifyRow.add(notification);

        InlineKeyboardButton desc = new InlineKeyboardButton();
        desc.setText(ButtonData.SWITCH_DESC_SENDING.getText() + (dto.isSendByDesc() ? "✅" : "❌"));
        desc.setCallbackData(ButtonData.SWITCH_DESC_SENDING.getData());
        descRow.add(desc);

        InlineKeyboardButton sendingCount = new InlineKeyboardButton();
        sendingCount.setText(ButtonData.HOW_MANY_SEND.getText() + dto.getAllFollowerCount() + " | " + dto.getSendFollowerCount());
        sendingCount.setCallbackData(ButtonData.HOW_MANY_SEND.getData());
        countRow.add(sendingCount);

        InlineKeyboardButton channelUsername = new InlineKeyboardButton();
        channelUsername.setText(ButtonData.SET_CHANNEL_USERNAME.getText() + dto.getChannelUsername());
        channelUsername.setCallbackData(ButtonData.SET_CHANNEL_USERNAME.getData());
        usernameRow.add(channelUsername);

        InlineKeyboardButton postId = new InlineKeyboardButton();
        postId.setText(ButtonData.SET_POST_ID.getText() + dto.getPostId());
        postId.setCallbackData(ButtonData.SET_POST_ID.getData());
        postIdRow.add(postId);

        InlineKeyboardButton htmlText = new InlineKeyboardButton();
        htmlText.setText(ButtonData.SET_HTML_TEXT.getText() + (dto.getHtmText() != null ? "✅" : "❌"));
        htmlText.setCallbackData(ButtonData.SET_HTML_TEXT.getData());
        hasHtmlTextRow.add(htmlText);

        InlineKeyboardButton inlineBoardForPost = new InlineKeyboardButton();
        inlineBoardForPost.setText(ButtonData.SET_CUSTOM_BOARD.getText() + (dto.getInlineKeyboardMarkup() != null ? "✅" : "❌"));
        inlineBoardForPost.setCallbackData(ButtonData.SET_CUSTOM_BOARD.getData());
        hasInlineKeyboardRow.add(inlineBoardForPost);

        InlineKeyboardButton test = new InlineKeyboardButton();
        test.setText(ButtonData.TEST_POST.getText());
        test.setCallbackData(ButtonData.TEST_POST.getData());
        testRow.add(test);

        rowList.add(notifyRow);
        rowList.add(descRow);
        rowList.add(countRow);
        rowList.add(usernameRow);
        rowList.add(postIdRow);
        rowList.add(hasHtmlTextRow);
        rowList.add(hasInlineKeyboardRow);
        rowList.add(testRow);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup lastQuestionBoard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        List<InlineKeyboardButton> startRow = new ArrayList<>();
        List<InlineKeyboardButton> resumeEditRow = new ArrayList<>();
        List<InlineKeyboardButton> killRow = new ArrayList<>();

        InlineKeyboardButton startBtn = new InlineKeyboardButton();
        startBtn.setText(ButtonData.START_POSTING.getText());
        startBtn.setCallbackData(ButtonData.START_POSTING.getData());
        startRow.add(startBtn);

        InlineKeyboardButton resumeEdit = new InlineKeyboardButton();
        resumeEdit.setText(ButtonData.RESUME_EDIT.getText());
        resumeEdit.setCallbackData(ButtonData.RESUME_EDIT.getData());
        resumeEditRow.add(resumeEdit);

        InlineKeyboardButton killBtn = new InlineKeyboardButton();
        killBtn.setText(ButtonData.KILL_PROCESS.getText());
        killBtn.setCallbackData(ButtonData.KILL_PROCESS.getData());
        killRow.add(killBtn);

        rowList.add(startRow);
        rowList.add(resumeEditRow);
        rowList.add(killRow);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public static List<InlineKeyboardButton> dtoToButton(Stream<PostButtonDto> dtoStream) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        dtoStream.forEach(buttonDto -> buttons.add(
                new InlineKeyboardButton(
                        buttonDto.getText(),
                        buttonDto.getUrl(),
                        null,
                        null,
                        null,
                        null,
                        false,
                        null)
        ));
        return buttons;
    }

    public static InlineKeyboardMarkup makeBoardFromDtoList(List<PostButtonDto> buttonDtoList) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        List<Integer> rows = buttonDtoList.stream().map(PostButtonDto::getRow).distinct().collect(Collectors.toList());

        for (Integer row : rows) {
            Stream<PostButtonDto> rowButtons = buttonDtoList.stream().filter(boardButtonDto -> boardButtonDto.getRow() == row).sorted(Comparator.comparing(PostButtonDto::getPosition));
            rowList.add(new ArrayList<>(Boards.dtoToButton(rowButtons)));
        }
        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    public static List<PostButtonDto> parseTextToButtonDtoList(String pattern) {
        try {
            if (!pattern.startsWith("{") || !pattern.endsWith("}")) return null;

            int curlyCharOpen = 0;
            int squadCharOpen = 0;
            int bracketCharOpen = 0;
            for (int i = 0; i < pattern.length(); i++) {
                if (pattern.charAt(i) == '{') curlyCharOpen++;
                else if (pattern.charAt(i) == '[') squadCharOpen++;
                else if (pattern.charAt(i) == '(') bracketCharOpen++;
                else if (pattern.charAt(i) == '}') curlyCharOpen--;
                else if (pattern.charAt(i) == ']') squadCharOpen--;
                else if (pattern.charAt(i) == ')') bracketCharOpen--;
            }
            if (bracketCharOpen != 0 || squadCharOpen != 0 || curlyCharOpen != 0) return null;

            List<PostButtonDto> buttonDtoList = new ArrayList<>();

            List<String> rows = new ArrayList<>();
            for (int i = 0; i < pattern.length(); i++) {
                if (pattern.charAt(i) == '[') {
                    int open = 1;
                    for (int j = i + 1; j < pattern.length(); j++) {
                        if (pattern.charAt(j) == '[') {
                            open++;
                            continue;
                        }
                        if (pattern.charAt(j) == ']' && open > 1) {
                            open--;
                            continue;
                        }

                        if (pattern.charAt(j) == ']' && open == 1) {
                            open--;
                            rows.add(
                                    pattern.substring(i, j + 1)
                            );
                            if (pattern.charAt(j + 1) == ',') {
                                pattern = (pattern.substring(0, i) + pattern.substring(j + 2));
                                i = 0;
                                break;
                            } else if (pattern.charAt(j + 1) != ',') {
                                pattern = (pattern.substring(0, i) + pattern.substring(j + 1));
                                i = 0;
                                break;
                            }
                        }
                    }
                }
            }
            for (int i = 0; i < rows.size(); i++) {
                String row = rows.get(i);
                String[] buttons = row.split(",");
                buttons[0] = buttons[0].substring(1);
                buttons[buttons.length - 1] = buttons[buttons.length - 1].substring(0, buttons[buttons.length - 1].length() - 1);
                for (int j = 0; j < buttons.length; j++) {
                    String button = buttons[j];
                    buttonDtoList.add(
                            new PostButtonDto(
                                    button.substring(
                                            button.indexOf('(') + 1,
                                            button.indexOf(')')
                                    ),
                                    button.substring(
                                            button.indexOf('[') + 1,
                                            button.indexOf(']')
                                    ),
                                    i + 1,
                                    j + 1
                            )
                    );
                }
            }
            return buttonDtoList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
