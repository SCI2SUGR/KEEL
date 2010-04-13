package keel.GraphInterKeel.experiments;

import java.awt.*;
import java.awt.geom.*;

public final class Jclec extends Node {

    protected Parameters param;

    public Jclec(ExternalObjectDescription dsc, Point position, GraphPanel p) {
        super(dsc, position, p.mainGraph.getId());
        p.mainGraph.setId(p.mainGraph.getId() + 1);
        type = type_Jclec;
        // subtipo = tipo_Jclec;
        image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/jclec.gif"));
        pd = p;
        param = new Parameters(dsc.getPath() + dsc.getName() + ".xml", false);
    }

    public Jclec(ExternalObjectDescription dsc, Point position, GraphPanel p,
            Parameters parameters, int id) {
        super(dsc, position, id);
        type = type_Jclec;
        image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/keel/GraphInterKeel/resources/ico/experiments/jclec.gif"));
        pd = p;
        param = new Parameters(parameters);
    }

    public void showDialog() {
        /*    dialogo = new ParametrosDialog(pd.padre, "Algorithm Parameters", true, param);

        // Center dialog
        dialogo.setSize(400, 580);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = dialogo.getSize();
        if (frameSize.height > screenSize.height) {
        frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
        frameSize.width = screenSize.width;
        }
        dialogo.setLocation( (screenSize.width - frameSize.width) / 2,
        (screenSize.height - frameSize.height) / 2);
        dialogo.setResizable(false);
        dialogo.setVisible(true);
         */
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
