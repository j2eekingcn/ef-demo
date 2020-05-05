package cn.br.ef.modules.sys.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.br.common.exception.UploadDownloadException;
import io.jooby.AttachedFile;
import io.jooby.Context;
import io.jooby.StatusCode;
import io.jooby.annotations.GET;
import io.jooby.annotations.Path;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

import static java.util.Objects.nonNull;

@Path("/mvc")
public class DownloadAct {

	private static final Logger log = LoggerFactory.getLogger(DownloadAct.class);
	private static final String JOOBY_LOGO = "logo_jooby.png";
	private static final String ERROR = "MVC Download error: %s.";

	@GET
	@Path("/download")
	public AttachedFile accept(Context ctx) {
		try {
//			final URL logo = this.getClass().getClassLoader().getResource(JOOBY_LOGO);
//			assert nonNull(logo);
//			final File file = new File(logo.toURI());

			java.nio.file.Path source = Paths.get("logo.png");
//			log.info("MVC Downloading file with size: " + source.length());
//			ctx.setResponseCode(StatusCode.OK).send(file)
//			response.status(200).download(file);
			return new AttachedFile(source);
		} catch (Throwable e) {
			final String error = String.format(ERROR, e.getMessage());
			throw new UploadDownloadException(error, e);
		}
	}
}
