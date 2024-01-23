interface ChatSession {
    createdAt:Date;
    sessionId: string;
    tokens:number;
    topic: string;
    userid: string;
 }


export{ChatSession}
