package io.github.bedwarsrevolution.blockdiguise;

import java.util.Collection;

/**
 * Created by {maxos} 2017
 */
public interface ChunkBlocksProcessor {

  /**
   * Processes blocks of one chunk.
   * Blocks from different chunks must not be passed.
   *
   * @param data
   */
  void process(Collection<BlockData> data);

}
