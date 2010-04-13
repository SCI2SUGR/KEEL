package keel.GraphInterKeel.experiments;

import java.awt.*;
import java.awt.geom.*;


public final class UserMethod extends Node {

    protected Parameters parametersUser;
    protected String patternFile;
    protected String command;

    public UserMethod(ExternalObjectDescription dsc, Point posicion,
            GraphPanel p) {
        super(dsc, posicion, p.mainGraph.getId());
        p.mainGraph.setId(p.mainGraph.getId() + 1);
        type = type_userMethod;
        patternFile = new String("");
        command = new String("");
        image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/usuario.gif"));
        pd = p;
        parametersUser = null;
    }

    public UserMethod(ExternalObjectDescription dsc, Point position,
            GraphPanel p, Parameters parameters, int id) {
        super(dsc, position, id);
        type = type_userMethod;
        patternFile = new String("");
        command = new String("");
        image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/usuario.gif"));
        pd = p;
        parametersUser = null;
        if (parameters != null) {
            parametersUser = new Parameters(parameters);
        }
    }

    public void showDialog() {

        dialog = new DialogUser(pd.parent, "Algorithm Files", true, this);
        dialog.setSize(400, 269);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = dialog.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        dialog.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    public void draw(Graphics2D g2, boolean select) {
        Point pinit = new Point(centre.x - 25, centre.y - 25);
        Point pfin = new Point(centre.x + 25, centre.y + 25);
        figure = new RoundRectangle2D.Float(pinit.x, pinit.y,
                Math.abs(pfin.x - pinit.x),
                Math.abs(pfin.y - pinit.y), 20, 20);

        g2.setColor(Color.black);
        if (select) {
            Stroke s = g2.getStroke();
            g2.setStroke(new BasicStroke(5, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL, 0, new float[]{1, 1}, 0));
            g2.draw(figure);
            g2.setStroke(s);
        } else {
            g2.draw(figure);

        }
        g2.drawImage(image, centre.x - 25, centre.y - 25, 50, 50, pd);

        g2.setFont(new Font("Courier", Font.BOLD + Font.ITALIC, 12));
        FontMetrics metrics = g2.getFontMetrics();
        int width = metrics.stringWidth(dsc.getName());
        int height = metrics.getHeight();
        g2.drawString(dsc.getName(), centre.x - width / 2, centre.y + 40);
    }
}
