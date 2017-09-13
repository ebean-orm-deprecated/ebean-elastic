package io.ebeanservice.elastic.update;

import io.ebean.DocStoreQueueEntry;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Collect and organise elastic updates grouping by queueId.
 */
public class ConvertToGroups {

  /**
   * Entries organised by queueId.
   */
  private final Map<String, UpdateGroup> byQueue = new LinkedHashMap<String, UpdateGroup>();

  public static Collection<UpdateGroup> groupByQueueId(List<DocStoreQueueEntry> queueEntries) {

    return new ConvertToGroups(queueEntries).groups();
  }

  /**
   * Add all the entries organising them by queueId and type.
   */
  private ConvertToGroups(List<DocStoreQueueEntry> queueEntries) {

    for (DocStoreQueueEntry entry : queueEntries) {
      getQueue(entry.getQueueId()).addEntry(entry);
    }
  }

  private UpdateGroup getQueue(String queueId) {
    UpdateGroup queue = byQueue.get(queueId);
    if (queue == null) {
      queue = new UpdateGroup(queueId);
      byQueue.put(queueId, queue);
    }
    return queue;
  }

  private Collection<UpdateGroup> groups() {
    return byQueue.values();
  }

}
