package com.mukunda.dungeons;
 
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class  WorldCopier {
	
	public static class Task extends BukkitRunnable {
		
		String world;
		Path tempPath;
		WorldCopyCallback onComplete;
		

		static class Copier extends SimpleFileVisitor<Path> {
			 
			private Path fromPath;
			private Path toPath; 
			
			@Override
			public FileVisitResult preVisitDirectory( Path dir, BasicFileAttributes attrs ) throws IOException {
				// create directory
				
				Path path = toPath.resolve( fromPath.relativize( dir ) );
				path.toFile().mkdirs();
				Files.createDirectories( dir );
				return FileVisitResult.CONTINUE; 
			}
			
			@Override
			public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) throws IOException {
				// copy file
				Path path = toPath.resolve( fromPath.relativize( file ) );
				Files.copy( file,  path );
	            return FileVisitResult.CONTINUE;
	        }
			
			Copier( Path pathFrom, Path pathTo ) throws IOException {
				fromPath = pathFrom;
				toPath = pathTo;
				
				Files.walkFileTree( fromPath, this );
			}
			    
		}
		
		public Task( String world, WorldCopyCallback onComplete ) {
			this.world = world;
			this.onComplete = onComplete;
		}
		
		public void run() {
			boolean failed = false;
			Dungeons.getContext().getLogger().info( "Copying dungeon world... \""+world+"\"" );
			Path newpath = Dungeons.getContext().getDataFolder().toPath().resolve("worlds");
			try {

				Files.createDirectories( newpath );
				newpath = Files.createTempDirectory( newpath, "instance" );
				new Copier( Paths.get(world), newpath );
			} catch( IOException e ) {
				failed = true;
				Dungeons.getContext().getLogger().warning( "Could not copy dungeon world. " + e.getMessage() );
			}
			
			onComplete.failed = failed;
			onComplete.newWorld = newpath;
			onComplete.runTask( Dungeons.getContext() );
		}
	}
	
	public static abstract class WorldCopyCallback extends BukkitRunnable {
		public Path newWorld;
		public boolean failed;
		
	}
	
	public static void copy( String world, WorldCopyCallback onComplete ) {
		new Task( world, onComplete ).runTaskAsynchronously( Dungeons.getContext() ); 
	}
}
