package com.crowdfundpro.android.ui.social;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.crowdfundpro.android.data.SocialRepository;
import com.crowdfundpro.android.data.models.Comment;
import com.crowdfundpro.android.data.models.User;
import com.crowdfundpro.android.utils.TokenManager;
import java.util.List;

/**
 * ViewModel pour les fonctionnalités sociales
 */
public class SocialViewModel extends ViewModel {
    
    private SocialRepository socialRepository;
    private TokenManager tokenManager;
    
    private MutableLiveData<List<Comment>> comments = new MutableLiveData<>();
    private MutableLiveData<User> userProfile = new MutableLiveData<>();
    private MutableLiveData<Boolean> commentPosted = new MutableLiveData<>();
    private MutableLiveData<Boolean> projectFollowed = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    
    public SocialViewModel() {
        // TODO: Injection de dépendances à implémenter
        // socialRepository = DependencyInjection.getSocialRepository();
        // tokenManager = DependencyInjection.getTokenManager();
        loading.setValue(false);
    }
    
    public LiveData<List<Comment>> getComments() {
        return comments;
    }
    
    public LiveData<User> getUserProfile() {
        return userProfile;
    }
    
    public LiveData<Boolean> getCommentPosted() {
        return commentPosted;
    }
    
    public LiveData<Boolean> getProjectFollowed() {
        return projectFollowed;
    }
    
    public LiveData<Boolean> getLoading() {
        return loading;
    }
    
    public LiveData<String> getError() {
        return error;
    }
    
    /**
     * Chargement des commentaires d'un projet
     */
    public void loadComments(int projectId) {
        loading.setValue(true);
        error.setValue(null);
        
        socialRepository.getProjectComments(projectId, new SocialRepository.CommentListCallback() {
            @Override
            public void onSuccess(List<Comment> commentList) {
                comments.setValue(commentList);
                loading.setValue(false);
            }
            
            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                loading.setValue(false);
            }
        });
    }
    
    /**
     * Publication d'un commentaire
     */
    public void postComment(int projectId, String commentText) {
        loading.setValue(true);
        error.setValue(null);
        commentPosted.setValue(false);
        
        String token = tokenManager.getAccessToken();
        if (token == null) {
            error.setValue("Token d'authentification manquant");
            loading.setValue(false);
            return;
        }
        
        socialRepository.postComment(token, projectId, commentText, new SocialRepository.CommentCallback() {
            @Override
            public void onSuccess(Comment comment) {
                commentPosted.setValue(true);
                loading.setValue(false);
            }
            
            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                loading.setValue(false);
            }
        });
    }
    
    /**
     * Chargement du profil d'un utilisateur
     */
    public void loadUserProfile(int userId) {
        loading.setValue(true);
        error.setValue(null);
        
        socialRepository.getUserProfile(userId, new SocialRepository.UserProfileCallback() {
            @Override
            public void onSuccess(User user) {
                userProfile.setValue(user);
                loading.setValue(false);
            }
            
            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
                loading.setValue(false);
            }
        });
    }
    
    /**
     * Suivi d'un projet
     */
    public void followProject(int projectId) {
        String token = tokenManager.getAccessToken();
        if (token == null) {
            error.setValue("Token d'authentification manquant");
            return;
        }
        
        socialRepository.followProject(token, projectId, new SocialRepository.FollowCallback() {
            @Override
            public void onSuccess(boolean isFollowing) {
                projectFollowed.setValue(isFollowing);
            }
            
            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
            }
        });
    }
    
    /**
     * Arrêt du suivi d'un projet
     */
    public void unfollowProject(int projectId) {
        String token = tokenManager.getAccessToken();
        if (token == null) {
            error.setValue("Token d'authentification manquant");
            return;
        }
        
        socialRepository.unfollowProject(token, projectId, new SocialRepository.FollowCallback() {
            @Override
            public void onSuccess(boolean isFollowing) {
                projectFollowed.setValue(isFollowing);
            }
            
            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
            }
        });
    }
    
    /**
     * Partage d'un projet
     */
    public void shareProject(int projectId, String platform) {
        // TODO: Implémenter le partage sur les réseaux sociaux
        // Cette méthode pourrait utiliser des intents Android pour partager
        // ou intégrer des SDKs de réseaux sociaux
    }
    
    /**
     * Signalement d'un commentaire
     */
    public void reportComment(int commentId, String reason) {
        String token = tokenManager.getAccessToken();
        if (token == null) {
            error.setValue("Token d'authentification manquant");
            return;
        }
        
        socialRepository.reportComment(token, commentId, reason, new SocialRepository.ReportCallback() {
            @Override
            public void onSuccess() {
                // Commentaire signalé avec succès
            }
            
            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
            }
        });
    }
    
    /**
     * Suppression d'un commentaire (si l'utilisateur en est l'auteur)
     */
    public void deleteComment(int commentId) {
        String token = tokenManager.getAccessToken();
        if (token == null) {
            error.setValue("Token d'authentification manquant");
            return;
        }
        
        socialRepository.deleteComment(token, commentId, new SocialRepository.DeleteCallback() {
            @Override
            public void onSuccess() {
                // Recharger les commentaires après suppression
                // TODO: Récupérer l'ID du projet depuis le contexte
            }
            
            @Override
            public void onError(String errorMessage) {
                error.setValue(errorMessage);
            }
        });
    }
}

