import java.util.*;
public final class TextChunker {
 public record Chunk(int index,int startCodePoint,int endCodePoint,String text) {}
 public static List<Chunk> chunk(String text,int size,int overlap){
  if(size<=0||overlap<0||overlap>=size)throw new IllegalArgumentException("require 0 <= overlap < size");
  int[] points=text.codePoints().toArray();var result=new ArrayList<Chunk>();
  for(int start=0;start<points.length;start+=size-overlap){int end=start+Math.min(size,points.length-start);result.add(new Chunk(result.size(),start,end,new String(points,start,end-start)));if(end==points.length)break;}
  return List.copyOf(result);
 }
 public static void main(String[] args){
  var chunks=chunk("abcdefgh",4,1);if(chunks.size()!=3||!chunks.get(1).text().equals("defg")||!chunks.get(2).text().equals("gh"))throw new AssertionError("overlap");
  if(chunk("",4,1).size()!=0)throw new AssertionError("empty");
  if(!chunk("A😀B",2,0).get(0).text().equals("A😀"))throw new AssertionError("unicode split");
  try{chunk("x",2,2);throw new AssertionError("invalid overlap accepted");}catch(IllegalArgumentException expected){}
  System.out.println("TextChunker self-tests passed");
 }
}
