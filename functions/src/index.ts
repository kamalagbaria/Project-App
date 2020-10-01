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
            const userId = context.params.userId;
            const userDoc = await admin.firestore().collection('users').doc(userId).get()
            const userDocData = userDoc.data();
            if(userDocData !== undefined)
            {
                const userEmail = userDocData.email
                const answerData = snapshot.data();
                if (answerData) {
                    const payload = {
                        data: {
                            name: answerData.name,
                            type: 'new-answer'
                        }
                    };
                    
                    return admin.messaging().sendToTopic(userEmail, payload);
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
