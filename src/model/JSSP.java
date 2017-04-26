package model;

import utils.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by audun on 26.04.17.
 */
public class JSSP {

    private static final JSSP jssp = new JSSP();
    public static JSSP getInstance() {
        if(!loaded) {
            throw new RuntimeException("JSSP problem not loaded.");
        }
        return jssp;
    }

    private static int numJobs, numMachines;
    private static List<Queue<Subtask>> jobs;

    private static boolean loaded = false;

    private JSSP() {}

    public static void loadFile(String filepath) throws IOException {
        FileReader file = new FileReader(filepath);
        BufferedReader bufReader = new BufferedReader(file);

        /* First line should contain numJobs (number of jobs), and numMachines (number of machines) */
        try {
            List<Integer> firstLine = StringUtils.parseLine(bufReader.readLine());
            numJobs = firstLine.get(0);
            numMachines = firstLine.get(1);
        } catch(Exception e) {
            throw new IOException("Could not parse first line. File may be invalid or corrupt.",e);
        }

        /* Following numJobs lines should contain a description of each job, listing the machine
         * number and processing time for each step of the job */
        jobs = new ArrayList();
        for(int i = 0; i < numJobs; ++i) {
            Queue<Subtask> job = new LinkedList();
            List<Integer> jobDescription;
            try {
                jobDescription = StringUtils.parseLine(bufReader.readLine());
            } catch(Exception e) {
                throw new IOException("Could not parse description of job " + i + ". File may be invalid or corrupt.",e);
            }
            for(int j = 0; j < numMachines; ++j) {
                job.add(new Subtask(jobDescription.get(2*j), jobDescription.get(2*j + 1)));
            }
            jobs.add(job);
        }
        bufReader.close();
        file.close();

        loaded = true;
    }

    public int getNumJobs() {
        return numJobs;
    }

    public int getNumMachines() {
        return numMachines;
    }

    public List<Queue<Subtask>> getJobs() {
        return jobs;
    }
}
