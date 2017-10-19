import agenda.presentation.UI;

public class AgendaService {
    public static void main(String[] args) {
        UI ui = UI.getInstance();

        while (true) {
            String cmd = ui.getCmd();
            ui.execCmd(cmd);
        }
    }
}