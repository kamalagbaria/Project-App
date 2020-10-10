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
            
            //const answerId = context.params.answerId;
            const userId = context.params.userId;
            const questionId = context.params.questionId;
            const questionNotifications = await admin.firestore().collection('users').doc(userId)
                    .collection("notifications").doc("question_notifications").get()

            const questionNotificationsData = questionNotifications.data();

            if(questionNotificationsData !== undefined)
            {
                if(!questionNotificationsData.newAnswer)
                {
                    return null;
                }
    
                const questionDoc = await admin.firestore().collection('users').doc(userId)
                                    .collection("questions").doc(questionId).get();
                        
                const questionDocData = questionDoc.data();
                const questionDocDataJson = JSON.stringify(questionDocData);

                    const answerData = snapshot.data();
                    if (answerData)
                    {
                        const answerId = answerData.answerId;
                        const userAnsweredData = (await admin.firestore().collection('users')
                            .doc(answerData.ownerId).get()).data()
                        
                        if(userAnsweredData !== undefined)
                        {
                            const message = {
                                data: {
                                    question: questionDocDataJson,
                                    userId: userId,
                                    questionId: questionId,
                                    answerId: answerId,
                                    name: userAnsweredData.fullName,
                                    type: 'new_answer_added'
                                }
                            };
                            return admin.messaging().sendToTopic(userId, message);
                        }
                        
                    }
                    else
                    {
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
            const commentId = context.params.commentId;
            const userId = context.params.userId;
            const questionId = context.params.questionId;

            const questionNotifications = await admin.firestore().collection('users').doc(userId)
            .collection("notifications").doc("question_notifications").get()

            const questionNotificationsData = questionNotifications.data();

            if(questionNotificationsData !== undefined)
            {
                if(!questionNotificationsData.newComment)
                {
                    return null;
                }
                    const commentData = snapshot.data();
                    if (commentData)
                    {
                        const userCommentData = (await admin.firestore().collection('users')
                            .doc(commentData.ownerId).get()).data()
                        
                        if(userCommentData !== undefined)
                        {
                            const message = {
                                data: {
                                    userId: userId,
                                    questionId: questionId,
                                    commentId: commentId,
                                    name: userCommentData.fullName,
                                    type: 'new_comment_added'
                                }
                            };
                            console.log(userId)
                            return admin.messaging().sendToTopic(userId, message);
                        }
                        
                    }
                    else
                    {
                        return null;
                    }
                
            }
        }
        catch(err)
        {
            return err;
        }
    });
