package org.lineageos.loscoins.db.column;

import android.provider.BaseColumns;

public interface CommitColumns extends BaseColumns {
    String BRANCH = "branch";
    String SUBJECT = "subject";
    String OWNER = "owner";
    String CREATION_DATE = "creation_date";
    String CLOSE_DATE = "close_date";
}