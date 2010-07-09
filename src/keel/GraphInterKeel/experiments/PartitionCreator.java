package keel.GraphInterKeel.experiments;

import javax.swing.SwingWorker;
import java.util.Vector;
import java.io.File;
import java.util.List;
import javax.swing.ProgressMonitor;

import keel.GraphInterKeel.datacf.partitionData.PartitionGenerator;

/**
 *
 * @author Ignacio Robles (29-4-2009)
 */
public class PartitionCreator extends SwingWorker<Boolean, Integer> {

    private DataSet ds;
    private Vector missingPartitions;
    private Experiments parent;
    private ProgressMonitor pm;

    PartitionCreator(Experiments parent, DataSet ds, ProgressMonitor pm) {
        super();
        this.ds = ds;
        this.missingPartitions = ds.getMissingVector();
        this.parent = parent;
        this.pm = pm;
    }

    @Override
    protected void process(List<Integer> progress) {
        Integer p = new Integer(progress.get(progress.size() - 1));
        pm.setProgress(p.intValue());
    }

    @Override
    protected Boolean doInBackground() {
        return doPartitions();
    }

    /**
     * Create the different kinds of partitions
     * @return true if the process completed successfully, false if cancelled
     */
    private synchronized Boolean doPartitions() {
        File fi;
        int counter = 0;

        //regenerate the partitions
        PartitionGenerator pg = new PartitionGenerator();
        for (int i = 0; i < missingPartitions.size() && !pm.isCanceled(); i++) {

            if (parent.cvType == parent.PK) {
                for (int l = 1; l <= parent.numberKFoldCross && !pm.isCanceled(); l++) {
                    fi = new File("." + ds.dsc.getPath(i) + ds.dsc.getName(i) + "/" + ds.dsc.getName(i) + "-" + parent.numberKFoldCross + "-" + l + "tra.dat");
                    if (fi.exists()) {
                        fi.delete();
                    }
                    fi = new File("." + ds.dsc.getPath(i) + ds.dsc.getName(i) + "/" + ds.dsc.getName(i) + "-" + parent.numberKFoldCross + "-" + l + "tst.dat");
                    if (fi.exists()) {
                        fi.delete();
                    }
                    counter++;
                    publish(new Integer(counter));
                }
                pg.partition(PartitionGenerator._K_FOLD, "." + ds.dsc.getPath(i) + ds.dsc.getName(i) + "/" + ds.dsc.getName(i) + ".dat", "." + ds.dsc.getPath(i) + ds.dsc.getName(i), parent.experimentGraph.getSeed(), parent.numberKFoldCross, -1);
            } else if (parent.cvType == Experiments.P5X2) {
                for (int l = 1; l <= parent.numberKFoldCross && !pm.isCanceled(); l++) {
                    fi = new File(ds.dsc.getName(i) + "-5x2-" + i + "tra.dat");
                    if (fi.exists()) {
                        fi.delete();
                    }
                    fi = new File(ds.dsc.getName(i) + "-5x2-" + i + "tst.dat");
                    if (fi.exists()) {
                        fi.delete();
                    }
                    counter++;
                    publish(new Integer(counter));

                }
                pg.partition(PartitionGenerator._5x2, "." + ds.dsc.getPath(i) + ds.dsc.getName(i) + "/" + ds.dsc.getName(i) + ".dat", "." + ds.dsc.getPath(i) + ds.dsc.getName(i), parent.experimentGraph.getSeed(), -1, -1);

            } else {
                pg.partition(PartitionGenerator._HOLDOUT, ds.dsc.name[i], ds.dsc.getPath(0), parent.experimentGraph.getSeed(), -1, 2);
            }

        }

        notify();
        if (!pm.isCanceled()) {
            return (new Boolean(true));
        } else {
            return (new Boolean(false));
        }
    }
}
