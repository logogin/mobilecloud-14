package org.magnum.mobilecloud.video.repository;

import static org.magnum.mobilecloud.video.client.VideoSvcApi.*;

import java.util.Collection;

import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * An interface for a repository that can store Video
 * objects and allow them to be searched by title.
 *
 * @author jules
 *
 */
// This @RepositoryRestResource annotation tells Spring Data Rest to
// expose the VideoRepository through a controller and map it to the
// "/video" path. This automatically enables you to do the following:
//
// 1. List all videos by sending a GET request to /video
// 2. Add a video by sending a POST request to /video with the JSON for a video
// 3. Get a specific video by sending a GET request to /video/{videoId}
//    (e.g., /video/1 would return the JSON for the video with id=1)
// 4. Send search requests to our findByXYZ methods to /video/search/findByXYZ
//    (e.g., /video/search/findByName?title=Foo)
//
public interface VideoRepository extends CrudRepository<Video, Long>{

	public Collection<Video> findByName(@Param("name") String name);

	public Collection<Video> findByDurationLessThan(@Param(DURATION_PARAMETER) long duration);


}