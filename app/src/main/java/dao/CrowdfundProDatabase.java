package com.crowdfundpro.android.data.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import com.crowdfundpro.android.data.models.User;
import com.crowdfundpro.android.data.models.Project;
import com.crowdfundpro.android.data.models.Investment;
import com.crowdfundpro.android.data.models.Category;

/**
 * Base de données Room pour CrowdfundPro
 */
@Database(
    entities = {User.class, Project.class, Investment.class, Category.class},
    version = 1,
    exportSchema = false
)
public abstract class CrowdfundProDatabase extends RoomDatabase {
    
    private static volatile CrowdfundProDatabase INSTANCE;
    
    // DAOs abstraits
    public abstract UserDao userDao();
    public abstract ProjectDao projectDao();
    public abstract InvestmentDao investmentDao();
    public abstract CategoryDao categoryDao();
    
    /**
     * Singleton pour obtenir l'instance de la base de données
     */
    public static CrowdfundProDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CrowdfundProDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        CrowdfundProDatabase.class,
                        "crowdfundpro_database"
                    )
                    .fallbackToDestructiveMigration() // Pour le développement uniquement
                    .build();
                }
            }
        }
        return INSTANCE;
    }
    
    /**
     * Méthode pour fermer la base de données (utile pour les tests)
     */
    public static void closeDatabase() {
        if (INSTANCE != null) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }
}

