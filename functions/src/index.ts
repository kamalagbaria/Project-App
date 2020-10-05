import * as functions from 'firebase-functions'
import * as admin from 'firebase-admin'
admin.initializeApp()
// Start writing Firebase Functions
// https://firebase.google.com/docs/functions/typescript

export const newAnswerSubmitted = functions.firestore
    .document('users/{userId}/questions/{questionId}/answers/{answerId}')
    .onCreate(async (snapshot, context) =>
    {
        
        try
        {
            const id = context.params.userId;
            const userDoc = await admin.firestore().collection('users').doc(id).get()
            const userDocData = userDoc.data();
            if(userDocData !== undefined)
            {
                const userId = userDocData.id
                const answerData = snapshot.data();
                if (answerData) {
                    const userAnsweredData = (await admin.firestore().collection('users')
                        .doc(answerData.ownerId).get()).data()
                    
                    if(userAnsweredData !== undefined)
                    {
                        const payload = {
                            data: {
                                name: userAnsweredData.fullName,
                                type: 'new-answer-added'
                            }
                        };
                        console.log(userId)
                        return admin.messaging().sendToTopic(userId, payload);
                    }
                    
                }
                else {
                    return null;
                }
            }
        }
        catch(err)
        {
            return err;
        }
    });

    export const newCommentSubmitted = functions.firestore
    .document('users/{userId}/questions/{questionId}/comments/{commentId}')
    .onCreate(async (snapshot, context) =>
    {
        
        try
        {
            const id = context.params.userId;
            const userDoc = await admin.firestore().collection('users').doc(id).get()
            const userDocData = userDoc.data();
            if(userDocData !== undefined)
            {
                const userId = userDocData.id
                const commentData = snapshot.data();
                if (commentData) {
                    const userCommentData = (await admin.firestore().collection('users')
                        .doc(commentData.ownerId).get()).data()
                    
                    if(userCommentData !== undefined)
                    {
                        const payload = {
                            data: {
                                name: userCommentData.fullName,
                                type: 'new-comment-added'
                            }
                        };
                        console.log(userId)
                        return admin.messaging().sendToTopic(userId, payload);
                    }
                    
                }
                else {
                    return null;
                }
            }
        }
        catch(err)
        {
            return err;
        }
    });
