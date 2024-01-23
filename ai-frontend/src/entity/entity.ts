interface ChatSession {
    createdAt:Date;
    sessionId: string|undefined;
    tokens:number;
    topic: string;
    userid: string;
 }

 interface ChatMessage {
    role:string;
    content:string;
    createdAt:Date;
 }

export{ChatSession, ChatMessage}
