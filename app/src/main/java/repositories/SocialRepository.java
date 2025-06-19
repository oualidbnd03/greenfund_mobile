package com.crowdfundpro.android.data;

import com.crowdfundpro.android.data.api.SocialApiService;
import com.crowdfundpro.android.data.db.CommentDao;
import com.crowdfundpro.android.data.models.Comment;
import com.crowdfundpro.android.data.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

/**
 * Repository pour les fonctionnalités sociales
 */
public class SocialRepository {
    
    private SocialApiService socialApiService;
    private CommentDao commentDao;
    
    public SocialRepository(SocialApiService socialApiService, CommentDao commentDao) {
        this.socialApiService = socialApiService;
        this.commentDao = commentDao;
    }
    
    /**
     * Interface pour les callbacks de commentaire unique
     */
    public interface CommentCallback {
        void onSuccess(Comment comment);
        void onError(String error);
    }
    
    /**
     * Interface pour les callbacks de liste de commentaires
     */
    public interface CommentListCallback {
        void onSuccess(List<Comment> comments);
        void onError(String error);
    }
    
    /**
     * Interface pour les callbacks de profil utilisateur
     */
    public interface UserProfileCallback {
        void onSuccess(User user);
        void onError(String error);
    }
    
    /**
     * Interface pour les callbacks de suivi
     */
    public interface FollowCallback {
        void onSuccess(boolean isFollowing);
        void onError(String error);
    }
    
    /**
     * Interface pour les callbacks de signalement
     */
    public interface ReportCallback {
        void onSuccess();
        void onError(String error);
    }
    
    /**
     * Interface pour les callbacks de suppression
     */
    public interface DeleteCallback {
        void onSuccess();
        void onError(String error);
    }
    
    /**
     * Récupération des commentaires d'un projet
     */
    public void getProjectComments(int projectId, CommentListCallback callback) {
        socialApiService.getProjectComments(projectId).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Comment> comments = response.body();
                    
                    // Sauvegarder les commentaires en local
                    new Thread(() -> commentDao.insertComments(comments)).start();
                    
                    callback.onSuccess(comments);
                } else {
                    callback.onError("Erreur lors de la récupération des commentaires: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                // En cas d'échec réseau, essayer de récupérer depuis la base locale
                new Thread(() -> {
                    List<Comment> localComments = commentDao.getCommentsByProject(projectId);
                    if (!localComments.isEmpty()) {
                        callback.onSuccess(localComments);
                    } else {
                        callback.onError("Erreur réseau: " + t.getMessage());
                    }
                }).start();
            }
        });
    }
    
    /**
     * Publication d'un commentaire
     */
    public void postComment(String token, int projectId, String content, CommentCallback callback) {
        SocialApiService.CommentRequest request = new SocialApiService.CommentRequest(projectId, content);
        
        socialApiService.postComment("Bearer " + token, request).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Comment comment = response.body();
                    
                    // Sauvegarder le commentaire en local
                    new Thread(() -> commentDao.insertComment(comment)).start();
                    
                    callback.onSuccess(comment);
                } else {
                    callback.onError("Erreur lors de la publication du commentaire: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                callback.onError("Erreur réseau: " + t.getMessage());
            }
        });
    }
    
    /**
     * Récupération du profil d'un utilisateur
     */
    public void getUserProfile(int userId, UserProfileCallback callback) {
        socialApiService.getUserProfile(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Erreur lors de la récupération du profil: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError("Erreur réseau: " + t.getMessage());
            }
        });
    }
    
    /**
     * Suivi d'un projet
     */
    public void followProject(String token, int projectId, FollowCallback callback) {
        socialApiService.followProject("Bearer " + token, projectId).enqueue(new Callback<SocialApiService.FollowResponse>() {
            @Override
            public void onResponse(Call<SocialApiService.FollowResponse> call, Response<SocialApiService.FollowResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().isFollowing());
                } else {
                    callback.onError("Erreur lors du suivi du projet: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<SocialApiService.FollowResponse> call, Throwable t) {
                callback.onError("Erreur réseau: " + t.getMessage());
            }
        });
    }
    
    /**
     * Arrêt du suivi d'un projet
     */
    public void unfollowProject(String token, int projectId, FollowCallback callback) {
        socialApiService.unfollowProject("Bearer " + token, projectId).enqueue(new Callback<SocialApiService.FollowResponse>() {
            @Override
            public void onResponse(Call<SocialApiService.FollowResponse> call, Response<SocialApiService.FollowResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().isFollowing());
                } else {
                    callback.onError("Erreur lors de l'arrêt du suivi: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<SocialApiService.FollowResponse> call, Throwable t) {
                callback.onError("Erreur réseau: " + t.getMessage());
            }
        });
    }
    
    /**
     * Signalement d'un commentaire
     */
    public void reportComment(String token, int commentId, String reason, ReportCallback callback) {
        SocialApiService.ReportRequest request = new SocialApiService.ReportRequest(reason);
        
        socialApiService.reportComment("Bearer " + token, commentId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Erreur lors du signalement: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Erreur réseau: " + t.getMessage());
            }
        });
    }
    
    /**
     * Suppression d'un commentaire
     */
    public void deleteComment(String token, int commentId, DeleteCallback callback) {
        socialApiService.deleteComment("Bearer " + token, commentId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Marquer le commentaire comme supprimé en local
                    new Thread(() -> commentDao.markCommentAsDeleted(commentId)).start();
                    
                    callback.onSuccess();
                } else {
                    callback.onError("Erreur lors de la suppression: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Erreur réseau: " + t.getMessage());
            }
        });
    }
    
    /**
     * Récupération des commentaires depuis la base de données locale
     */
    public List<Comment> getLocalComments(int projectId) {
        return commentDao.getCommentsByProject(projectId);
    }
}

