package ch.fhnw.digi.mockups.case3.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.JComboBox;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.fhnw.digi.mockups.case3.JobAssignmentMessage;
import ch.fhnw.digi.mockups.case3.JobMessage;

@SuppressWarnings("serial")
@Component
public class UI extends JFrame {

	@Autowired
	private MessageSender messageSender;

	private JList<JobMessage> m_list_jobs;
	private DefaultListModel<JobMessage> m_list_jobs_model;

	private JList<String> m_list_assignments;
	private DefaultListModel<String> m_list_assignments_model;
    private List<JobMessage> allJobs;

	private JButton m_btn_requestJob;
    private JComboBox<String> m_regionFilter;

	@PostConstruct
	void init() {

        allJobs = new ArrayList<>();
        m_list_jobs_model = new DefaultListModel<JobMessage>();
		m_list_jobs = new JList<JobMessage>(m_list_jobs_model);

		m_list_assignments_model = new DefaultListModel<String>();
		m_list_assignments = new JList<String>(m_list_assignments_model);

		m_btn_requestJob = new JButton("Selektierten Job anfordern");
        m_btn_requestJob.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                acceptSelectedJob();
            }
        });

        m_regionFilter = new JComboBox<>();
        m_regionFilter.addItem("Alle");
        m_regionFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshJobList();
            }
        });


		JPanel rootPanel = new JPanel(new BorderLayout());
		JPanel listsPanel = new JPanel();
		listsPanel.setLayout(new BoxLayout(listsPanel, BoxLayout.X_AXIS));
		JPanel jobsPanel = new JPanel(new BorderLayout());
		JPanel assignmentsPanel = new JPanel(new BorderLayout());
		
		
		jobsPanel.add(new JLabel("offene Jobs:"),BorderLayout.NORTH);
		
		
		jobsPanel.add(new JScrollPane(m_list_jobs), BorderLayout.CENTER);
		jobsPanel.setMinimumSize(new Dimension(200,200));
		assignmentsPanel.add(new JLabel("zugewiesene Jobs:"),BorderLayout.NORTH);
		assignmentsPanel.add(new JScrollPane(m_list_assignments), BorderLayout.CENTER);
		assignmentsPanel.setMinimumSize(new Dimension(200,200));
		listsPanel.add(jobsPanel);
		listsPanel.add(assignmentsPanel);
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("Region:"));
        bottomPanel.add(m_regionFilter);
        bottomPanel.add(m_btn_requestJob);
        rootPanel.add(bottomPanel, BorderLayout.SOUTH);
		rootPanel.add(listsPanel,BorderLayout.CENTER);

		
		
		getContentPane().add(rootPanel);

		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

    protected void acceptSelectedJob() {
        JobMessage m = m_list_jobs.getSelectedValue();
        if (m == null)
            return;

        messageSender.requestJobFromDispo(m);

        synchronized (allJobs) {
            allJobs.remove(m);
        }
        refreshJobList();
    }

    private void refreshJobList() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String selectedRegion = (String) m_regionFilter.getSelectedItem();

                synchronized (m_list_jobs_model) {
                    m_list_jobs_model.clear();

                    for (JobMessage job : allJobs) {
                        if ("Alle".equals(selectedRegion) || job.getRegion().equals(selectedRegion)) {
                            m_list_jobs_model.addElement(job);
                        }
                    }
                }
            }
        });
    }

    private void updateRegionFilter(JobMessage job) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String region = job.getRegion();

                for (int i = 0; i < m_regionFilter.getItemCount(); i++) {
                    if (m_regionFilter.getItemAt(i).equals(region)) {
                        return;
                    }
                }

                m_regionFilter.addItem(region);
            }
        });
    }

    public void addJobToList(JobMessage j) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                synchronized (allJobs) {
                    allJobs.add(0, j);
                }
                updateRegionFilter(j);
                refreshJobList();
            }
        });
    }

    public void assignJob(JobAssignmentMessage c) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                synchronized (allJobs) {
                    for (int x = 0; x < allJobs.size(); ++x) {
                        if (allJobs.get(x).getJobnumber().equals(c.getJobnumber())) {
                            allJobs.remove(x);
                            break;
                        }
                    }
                }

                refreshJobList();

                synchronized (m_list_assignments_model) {
                    m_list_assignments_model.add(0,
                            "Job " + c.getJobnumber() + " wurde an \"" + c.getAssignedEmployee() + "\" vergeben");
                }
            }
        });
    }
}
