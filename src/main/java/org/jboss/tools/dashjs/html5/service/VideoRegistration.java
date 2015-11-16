package org.jboss.tools.dashjs.html5.service;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.jboss.tools.dashjs.html5.model.Video;
import com.castlabs.dash.dashfragmenter.sequences.DashFileSetSequence;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.net.URL;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class VideoRegistration {
	
	 @Inject
	    private Logger log;
	 
	 @Inject
	    private EntityManager em;
	 
	 @Inject
	    private Event<Video> videoEventSrc;
	
	private List<File> closedCaptions;
	private List<File> LOF;
	private List<File> reducedFramerateReps;
	private List<File> vtt;

	// Upon registration of the video, we dash it.
	public int register(Video member) throws Exception {
		
		log.info("Registering " + member.getName());
        member.setMpdURI("http://192.168.0.16/samples/dash-videos/" + member.getName() + "/Manifest.mpd");
		em.persist(member);
        videoEventSrc.fire(member);
		
		DashFileSetSequence dashFileSetSequence = new DashFileSetSequence();
		
		// Determine wheather or not to explode into several files.
		switch (member.getExplode()) {
		case "on": dashFileSetSequence.setExplode(true);
			break;
		case "off": dashFileSetSequence.setExplode(false);			
			break;
		default:
			dashFileSetSequence.setExplode(false);;
		}
		
        dashFileSetSequence.setOutputDirectory(
        		new File("C:/dev/dash.js-1.5.1/samples/dash-videos/" + member.getName()));
        
        log.info("Creating List of Videos to include...");
        LOF = new ArrayList<File>();
        
        //Convert each URI to a File, add to List of Files. (LOF)
        String[] URIs = member.getURI().split(";");
        
        ArrayList<File> filesToCleanup = new ArrayList<File>();
        
        for (String videoUri : URIs) {
        	
        	if(videoUri.startsWith("http")) {
        		log.info("Downloading : " + videoUri);
            	// Temporary File Instance using filename.
        		String[] pathbits = videoUri.split("/");
        		String name = pathbits[pathbits.length-1];
        		File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + name);
        		// Download
            	FileUtils.copyURLToFile(new URL(videoUri), tempFile);
	        	log.info("Adding to List : " + videoUri);
                LOF.add(tempFile);
                filesToCleanup.add(tempFile);
        	} 
        	else {
        		//if !startsWith HTTP(S) assuming local.
	        	log.info("Adding to List : " + videoUri);
		    	LOF.add(new File(videoUri)); 
		    }
        	
        }
        //
        dashFileSetSequence.setInputFiles(LOF);
        
        dashFileSetSequence.setSubtitles(vtt);
        
        dashFileSetSequence.setTrickModeFiles(reducedFramerateReps);
        
        dashFileSetSequence.setClosedCaptions(closedCaptions);
        
        log.info("Starting Dash Segment Encoder...");
        
        int response = dashFileSetSequence.run();
        
        log.info("Conversion Complete.");
        log.info("Cleaning up temporary file...");
        for (File file : filesToCleanup) {
			file.delete();
		}
        
        return response;
		
	}
	
}