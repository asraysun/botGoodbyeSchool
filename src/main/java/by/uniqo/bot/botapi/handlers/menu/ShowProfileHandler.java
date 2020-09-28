package by.uniqo.bot.botapi.handlers.menu;

import by.uniqo.bot.botapi.handlers.BotState;
import by.uniqo.bot.botapi.handlers.InputMessageHandler;
import by.uniqo.bot.botapi.handlers.fillingOrder.UserProfileData;
import by.uniqo.bot.cache.UserDataCache;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * @author has been inspired by Sergei Viacheslaev's work
 */
@Component
public class ShowProfileHandler implements InputMessageHandler {
    private UserDataCache userDataCache;

    public ShowProfileHandler(UserDataCache userDataCache) {
        this.userDataCache = userDataCache;
    }

    @Override
    public SendMessage handle(Message message) {
        final int userId = message.getFrom().getId();
        final UserProfileData profileData = userDataCache.getUserProfileData(userId);

        userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
        return new SendMessage(message.getChatId(), String.format( "%s%nОбщая сумма лент: %s%nЦвет лент: %s%nНомер макета: %s%nЦвет текста макета:" +
                        " %s%nНомер символа на ленте: %s%nКол-во парней: %s%nКол-во девушек: %s%nКлассный руководитель: %s%nНомер школы %s%nСтразы: " +
                        "%s%nМаленький колокольчик:  %s%nБольшой колокольчик:" +
                        "  %s%nПригласительный свиток:  %s%nБант:  %s%nБабочка: " +
                        " %s%nФИО %s%nТелефонный номер:" +
                        " %s%nАдрес доставки %s%nКомментарии к заказу %s",
                "Данные по вашему заказу", profileData.getTotalNumber(), profileData.getTapesColor(), profileData.getModelNumber(),
                profileData.getColorOfModelText(), profileData.getSymbolNumber(), profileData.getNumberOfMen(), profileData.getNumberOfWomen(),
                profileData.getNumberOfTeacher(), profileData.getSchoolNumber(),profileData.getStars(),profileData.getLittleBell(),
                profileData.getBigBell(), profileData.getScroll(),
                profileData.getRibbon(), profileData.getBowtie(),
                profileData.getCredentials(), profileData.getPhoneNumber(),
                profileData.getDeliveryAddress(), profileData.getCommentsToOrder()));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_USER_ORDER;
    }
}