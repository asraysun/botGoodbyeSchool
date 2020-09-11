package by.uniqo.bot.botapi.handlers;

import by.uniqo.bot.botapi.handlers.fillingOrder.UserProfileData;
import by.uniqo.bot.cache.UserDataCache;
import by.uniqo.bot.service.MainMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author get inspired by Sergei Viacheslaev's video
 */
@Component
@Slf4j
public class TelegramFacade {
    private BotStateContext botStateContext;
    private UserDataCache userDataCache;
    private MainMenuService mainMenuService;

    public TelegramFacade(BotStateContext botStateContext, UserDataCache userDataCache, MainMenuService mainMenuService) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
        this.mainMenuService = mainMenuService;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
            return processCallbackQuery(callbackQuery);
        }


        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, userId: {}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getFrom().getId(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        int userId = message.getFrom().getId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg) {
            case "/start":
                botState = BotState.ASK_START;
                break;
            case "Сделать заказ":
                botState = BotState.FILLING_ORDER;
                break;
            case "Мой заказ":
                botState = BotState.SHOW_USER_ORDER;
                break;
            case "Помощь":
                botState = BotState.SHOW_HELP_MENU;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;
        }

        userDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }


    private BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final int userId = buttonQuery.getFrom().getId();
        BotApiMethod<?> callBackAnswer = mainMenuService.getMainMenuMessage(chatId, "Воспользуйтесь главным меню");


        //From Destiny choose buttons
        if (buttonQuery.getData().equals("buttonYes")) {
            callBackAnswer = new SendMessage(chatId, "Укажите общее количество лент вместе с классным руководителем");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_TAPESCOLOR);
        } else if (buttonQuery.getData().equals("buttonNo")) {
            callBackAnswer = sendAnswerCallbackQuery("Возвращайся, когда будешь готов", false, buttonQuery);
        } else if (buttonQuery.getData().equals("buttonIwillThink")) {
            callBackAnswer = sendAnswerCallbackQuery("Данная кнопка не поддерживается", true, buttonQuery);
            //From menus in additional services
        }  else if (buttonQuery.getData().equals("buttonStars")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setStars("Да");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
            callBackAnswer = sendAnswerCallbackQuery("Что-нибудь еще?", true, buttonQuery);

        } else if (buttonQuery.getData().equals("buttonScroll")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setScroll("Да");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_SCROLLCOLOR);
            callBackAnswer = new SendMessage(chatId, "Выберите цвет пригласительного свитка");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);

        } else if (buttonQuery.getData().equals("buttonBigBell")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setBigBell("Да");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_BIGBELLCOLOR);
            callBackAnswer = new SendMessage(chatId, "Выверите цвет большого колокольчика");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);

        } else if (buttonQuery.getData().equals("buttonLittleBell")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setLittleBell("Да");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_LITTLEBELLCOLOR);
            callBackAnswer = new SendMessage(chatId, "Выберите цвет маленького колокольчика");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);

        } else if (buttonQuery.getData().equals("buttonRibbon")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setRibbon("Да");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_RIBBONCOLOR);
            callBackAnswer = new SendMessage(chatId, "Выберите цвет банта");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);

        } else if (buttonQuery.getData().equals("buttonBowtie")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setBowtie("Да");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_BOWTIECOLOR);
            callBackAnswer = new SendMessage(chatId, "Выберите цвет бабочки");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);

        } else if (buttonQuery.getData().equals("buttonNext")) {
//            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
//            userProfileData.setButtonBowtie("Yes");
//            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_CREDENTIALS);
            callBackAnswer = new SendMessage(chatId, "Укажите свою ФИО");
        }



        //From ModelColorText choose buttons
        else if (buttonQuery.getData().equals("buttonOne")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setColorOfModelText(1);
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBEROFMEN);
            callBackAnswer = new SendMessage(chatId, "Выберите номер символа");
        } else if (buttonQuery.getData().equals("button2")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setColorOfModelText(2);
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBEROFMEN);
            callBackAnswer = new SendMessage(chatId, "Выберите номер символа");

        } else if (buttonQuery.getData().equals("button3")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setColorOfModelText(3);
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBEROFMEN);
            callBackAnswer = new SendMessage(chatId, "Выберите номер символа");

        } else if (buttonQuery.getData().equals("button4")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setColorOfModelText(4);
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBEROFMEN);
            callBackAnswer = new SendMessage(chatId, "Выберите номер символа");

        } else if (buttonQuery.getData().equals("button5")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setColorOfModelText(5);
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBEROFMEN);
            callBackAnswer = new SendMessage(chatId, "Выберите номер символа");

        } else {
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
        }


        return callBackAnswer;


    }


    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }


}