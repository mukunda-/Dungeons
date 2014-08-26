package com.mukunda.dungeons;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.bukkit.scheduler.BukkitRunnable; 

public abstract class FolderDeleter {
	
	public static class Task extends BukkitRunnable {
		
		private String path;
		  
		static class Deleter extends SimpleFileVisitor<Path> {
			  
			@Override
			public FileVisitResult preVisitDirectory( Path dir, BasicFileAttributes attrs ) throws IOException {
				// create directory 
				Files.delete( dir );
				return FileVisitResult.CONTINUE; 
			}
			
			@Override
			public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) throws IOException {
				// copy file
				Files.delete( file ); 
	            return FileVisitResult.CONTINUE;
	        }
			
			Deleter( Path path ) throws IOException {
				Files.walkFileTree( path, this );
			}
			    
		}
		
		public Task( String path ) {
			this.path = path; 
		}
		
		public void run() { 
			Dungeons.getContext().getLogger().info( "Deleting folder... \""+path+"\"" );
			Path newpath = Dungeons.getContext().getDataFolder().toPath().resolve("worlds");
			try {
				
				Files.createDirectories( newpath );
				newpath = Files.createTempDirectory( newpath, "instance" );
				new Deleter( Paths.get(path) );
			} catch( IOException e ) {
				 
				Dungeons.getContext().getLogger().warning( "Could not delete folder. " + e.getMessage() );
			}
		}
	}
 
	public static void delete( String path ) {
		new Task( path ).runTaskAsynchronously( Dungeons.getContext() ); 
	}
}
